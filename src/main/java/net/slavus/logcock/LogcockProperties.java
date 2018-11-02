package net.slavus.logcock;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "logcock")
@Validated
public class LogcockProperties {

  /**
   * List of hosted folder to view.
   */
  private List<HostedFolder> folders = new ArrayList<>();


  public List<HostedFolder> getFolders() {
    return folders;
  }

  public void setFolders(List<HostedFolder> folders) {
    this.folders = folders;
  }

  public static class HostedFolder {

    /**
     * Name of base hosted folder.
     */
    @NotBlank
    private String name;
    /**
     * Base path that hosts files to view.
     */
    @NotBlank
    private String basePath;

    public boolean isDirectory() {
      return true;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getBasePath() {
      return basePath;
    }

    public void setBasePath(String basePath) {
      this.basePath = basePath;
    }


  }

}
