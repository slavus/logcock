package net.slavus.logcock.tail;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FileTailer{
  private static final Logger LOGGER = LoggerFactory.getLogger(FileTailer.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    FileTailer ft = new FileTailer(Executors.newFixedThreadPool(1), "logcock.log");
    Flux<String> flux = ft.fileTailFlux();
    flux.subscribe(System.out::println);
  }

  private long lastKnownPosition = 0;
  private volatile boolean cancled = false;
  File tailedFile;
  private ExecutorService executorService;

  public FileTailer(ExecutorService executorService, String filename) {
    this.executorService = executorService;
    tailedFile = new File(filename);
  }

  public Flux<String> fileTailFlux() {
    return Flux.create(sink -> {
      Disposable onExit= () -> cancled = true;

      sink.onCancel(onExit);
      sink.onDispose(onExit);

      executorService.execute(() -> {
        try {
          watchFile(sink);
        } catch (InterruptedException | IOException e) {
          sink.error(e);
        }
      });
    });
  }

  private void watchFile(FluxSink<String> sink) throws IOException, InterruptedException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    WatchKey watchedKey = tailedFile.getAbsoluteFile().getParentFile().toPath().register(watchService, ENTRY_MODIFY);

    WatchKey key;
    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        if (Files.isSameFile(tailedFile.getAbsoluteFile().toPath(), (Path) event.context())) {
          List<String> readLastLines = readLastLines();
          if (!readLastLines.isEmpty()) {
            String lines = readLastLines.stream().collect(Collectors.joining("\n"));
            LOGGER.debug("New lines: {}", lines);
            sink.next(lines);
          }
        }
      }
      key.reset();
      if (cancled) {
        break;
      }
    }
    watchedKey.cancel();
  }

  private List<String> readLastLines() throws IOException {
    List<String> lines = Lists.newArrayList();

    long fileLength = tailedFile.length();
    if (fileLength > lastKnownPosition) {

      // Reading and writing file
      RandomAccessFile readWriteFileAccess;
      readWriteFileAccess = new RandomAccessFile(tailedFile, "rw");
      readWriteFileAccess.seek(lastKnownPosition);
      String crunentLine = null;
      while ((crunentLine = readWriteFileAccess.readLine()) != null) {
        lines.add(crunentLine);
      }
      lastKnownPosition = readWriteFileAccess.getFilePointer();
      readWriteFileAccess.close();

    }
    return lines;
  }

}
