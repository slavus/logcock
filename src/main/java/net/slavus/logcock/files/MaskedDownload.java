package net.slavus.logcock.files;

public class MaskedDownload {

  private String token;

  MaskedDownload() {
  }

  /**
   * @param token
   */
  MaskedDownload(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
