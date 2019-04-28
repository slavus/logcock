package net.slavus.logcock.tail;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;

import net.slavus.logcock.LogcockProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class FileTailControlerWSHandler implements WebSocketHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileTailControlerWSHandler.class);


  private ExecutorService tailExecutorService = Executors.newCachedThreadPool();


  @Autowired
  private LogcockProperties properties;

  @GetMapping("/t/{id}/{*webPath}")
  private String tailFile(@PathVariable Integer id, @PathVariable String webPath, Model model, ServerHttpRequest req) {
    model.addAttribute("wsPath", "/tail/" + id + "/" + webPath);
    return "tail";
  }

  private String filePath(Integer id, String webPath) {
    String basePath = properties.getFolders().get(id).getBasePath();
    String filePath = basePath + File.separator + webPath;
    return filePath;
  }


  @Override
  public Mono<Void> handle(WebSocketSession session) {
    LOGGER.info("Open websocket conection...");

    UriTemplate template = new UriTemplate("/tail/{id}/{path:.+}");
    Map<String, String> parameters = template.match(
        session.getHandshakeInfo().getUri().toString());
    Integer id = Integer.valueOf(parameters.get("id"));
    String path = parameters.get("path");
    String filename = filePath(id, path);

    Flux<String> fileTailFlux = new FileTailer(tailExecutorService, filename)
        .fileTailFlux();

    Mono<Void> input = session.receive()
        .doOnComplete(() -> {
          LOGGER.info("Close Websocket conection...");
          session.close();
        })
        .then();

    Mono<Void> output = session.send(fileTailFlux.map(session::textMessage));

    return Mono.zip(input, output).then();
  }
}