package net.slavus.logcock.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

@Service
public class MaskedDownloadService {

  private static final int SPLIT_SIZE = 100*1024;

  public MaskedDownload maskedDownload(String filePath, Integer part) {
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);
    try {
      InputStream inputStream = fileSystemResource.getInputStream();
      inputStream.skip(SPLIT_SIZE*part);
      byte[] bytes = IOUtils.toByteArray(fileSystemResource.getInputStream());
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      String token = base64Encoded.substring(part*SPLIT_SIZE, Math.min((part+1)*SPLIT_SIZE, base64Encoded.length()));
      return new MaskedDownload(token);
    } catch (IOException e) {
      return ExceptionUtils.rethrow(e);
    }
  }
  public MaskedDownload maskedDownloadSplit(String filePath, Integer part) {
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);
    try {
      byte[] bytes = IOUtils.toByteArray(fileSystemResource.getInputStream());
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      String token = base64Encoded.substring(part*SPLIT_SIZE, Math.min((part+1)*SPLIT_SIZE, base64Encoded.length()));
      return new MaskedDownload(token);
    } catch (IOException e) {
      return ExceptionUtils.rethrow(e);
    }
  }

  public MaskedDownloadInfo maskedDownloadInfo(String filePath) {
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);
    try {
      byte[] bytes = IOUtils.toByteArray(fileSystemResource.getInputStream());
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      String base64Filename = Base64.getEncoder().encodeToString(fileSystemResource.getFilename().getBytes());

      return new MaskedDownloadInfo(base64Filename,  base64Encoded.length(), (base64Encoded.length()/SPLIT_SIZE+1));
    } catch (IOException e) {
      return ExceptionUtils.rethrow(e);
    }
  }
}
