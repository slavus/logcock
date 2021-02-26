package net.slavus.logcock.files;

public class MaskedDownloadInfo {

  private String name;
  private Long size;
  private Long parts;

  MaskedDownloadInfo() { }

  /**
   * @param name
   * @param size
   * @param parts
   */
  MaskedDownloadInfo(String name, Long size, Long parts) {
    this.name = name;
    this.size = size;
    this.parts = parts;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getParts() {
    return parts;
  }

  public void setParts(Long parts) {
    this.parts = parts;
  }




}
