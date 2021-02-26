package net.slavus.logcock.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

@Service
public class MaskedDownloadService {

  private static final int SPLIT_SIZE = 250*1024;

  public MaskedDownload maskedDownloadSplit(String filePath, Integer part) {
    FileSystemResource fileSystemResource = new FileSystemResource(filePath + ".b64");
    try {
      byte[] buffer = new byte[SPLIT_SIZE];
      InputStream inputStream = fileSystemResource.getInputStream();
      inputStream.skip(SPLIT_SIZE * part);
      int len = IOUtils.read(inputStream, buffer);
      String token = new String(buffer,0, len);
      inputStream.close();
      deleteB64AtEnd(part, fileSystemResource);
      return new MaskedDownload(token);
    } catch (IOException e) {
      return ExceptionUtils.rethrow(e);
    }
  }

  private void deleteB64AtEnd(Integer part, FileSystemResource fileSystemResource) {
    System.out.println("SPLIT_SIZE*part:" + SPLIT_SIZE * part + " File len:" + fileSystemResource.getFile().length());
    if ((SPLIT_SIZE * part+1) >= fileSystemResource.getFile().length()) {
      fileSystemResource.getFile().delete();
    }
  }

  public MaskedDownload maskedDownloadWhole(String filePath, Integer part) {
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);
    try {
      byte[] bytes = IOUtils.toByteArray(fileSystemResource.getInputStream());
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      String token = base64Encoded.substring(part * SPLIT_SIZE,
          Math.min((part + 1) * SPLIT_SIZE, base64Encoded.length()));
      return new MaskedDownload(token);
    } catch (IOException e) {
      return ExceptionUtils.rethrow(e);
    }
  }

  public MaskedDownloadInfo maskedDownloadInfo(String filePath) {
    FileSystemResource fileSystemResourceOrig = new FileSystemResource(filePath);
    FileSystemResource fileSystemResourceB64 = new FileSystemResource(filePath + ".b64");
    if (!fileSystemResourceB64.exists() || !fileSystemResourceOrig.getFilename().endsWith(".b64")) {
      try {
        fileSystemResourceB64.getFile().createNewFile();
        OutputStream wrap = Base64.getEncoder().wrap(fileSystemResourceB64.getOutputStream());
        IOUtils.copy(fileSystemResourceOrig.getInputStream(), wrap);
      } catch (IOException e) {
        return ExceptionUtils.rethrow(e);
      }
    }

    String base64Filename = Base64.getEncoder().encodeToString(fileSystemResourceB64.getFilename().getBytes());

    return new MaskedDownloadInfo(base64Filename, fileSystemResourceB64.getFile().length(),
        fileSystemResourceB64.getFile().length() / SPLIT_SIZE + 1);
  }
}
