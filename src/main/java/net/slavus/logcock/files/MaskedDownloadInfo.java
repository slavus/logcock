package net.slavus.logcock.files;

public class MaskedDownloadInfo {

  private String name;
  private Integer size;
  private Integer parts;

  MaskedDownloadInfo() { }

  /**
   * @param name
   * @param size
   * @param parts
   */
  MaskedDownloadInfo(String name, Integer size, Integer parts) {
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

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getParts() {
    return parts;
  }

  public void setParts(Integer parts) {
    this.parts = parts;
  }




}
