package net.slavus.logcock.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Files {

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

  public static String owner(File file) {
    try {
      return java.nio.file.Files.getOwner(file.toPath()).getName();
    } catch (IOException e) {
      return "?????";
    }
  }
  public static String size(File file) {
    return humanReadableByteCount(file.length(), false);
  }

  public static Date lastModified(File file) {
    return new Date(file.lastModified());
  }
  public static String type(File file) {
    try {
      return java.nio.file.Files.probeContentType(file.toPath());
    } catch (IOException e) {
      return "?????";
    }
  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
