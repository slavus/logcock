package net.slavus.logcock.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.slavus.logcock.LogcockProperties;
import net.slavus.logcock.LogcockProperties.HostedFolder;

@Component
public class Files {
  @Autowired
  LogcockProperties logcockProperties;

  /**
   * Retutrns Posix style file permissions
   *
   * @param file input file
   * @return {@link String} representation of a set of permissions. It is
   *         guaranteed that the returned String can be parsed by the fromString
   *         method
   */
  public String permission(File file) {
    try {
      return PosixFilePermissions.toString(java.nio.file.Files.getPosixFilePermissions(file.toPath()));
    } catch (IOException e) {
      return "????";
    }
  }

  public String owner(File file) {
    try {
      return java.nio.file.Files.getOwner(file.toPath()).getName();
    } catch (IOException e) {
      return "?????";
    }
  }

  public String size(File file) {
    return humanReadableByteCount(file.length(), false);
  }

  public Date lastModified(File file) {
    return new Date(file.lastModified());
  }

  public String type(File file) {
    try {
      return java.nio.file.Files.probeContentType(file.toPath());
    } catch (IOException e) {
      return "?????";
    }
  }

  public String linkPath(File file, String basePath) {
    return StringUtils.removeStart(file.getPath(), basePath);
  }

  public String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }



  public String linkPath(HostedFolder folder, String basePath) {
    return logcockProperties.getFolders().indexOf(folder) + "/";
  }
  public String type(HostedFolder file) { return ""; }
  public Date lastModified(HostedFolder file) { return new Date(); }
  public String size(HostedFolder file) { return ""; }
  public String owner(HostedFolder file) { return ""; }
  public String permission(HostedFolder file) { return ""; }
}
