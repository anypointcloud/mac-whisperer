package org.mule.extension.whisperer.internal.connection.whisperjni;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

public class WhisperJNIRemoteModelParameters {

  @Parameter
  @DisplayName("Repository url")
  @Expression(ExpressionSupport.SUPPORTED)
  @Placement(order = 1)
  @Example("https://huggingface.co/ggerganov/whisper.cpp/blob/main/ggml-large-v3-turbo-q8_0.bin")
  private String modelURL;

  @Parameter
  @DisplayName("Download path")
  @Expression(ExpressionSupport.SUPPORTED)
  @Placement(order = 2)
  @Example("mule.home ++ \"/apps/\" ++ app.name ++ \"/\"")
  private String downloadPath;

  public String getModelURL() {
    return modelURL;
  }

  public void setModelURL(String modelURL) {
    this.modelURL = modelURL;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public void setDownloadPath(String downloadPath) {
    this.downloadPath = downloadPath;
  }

  public String getModelFilePath() {
    String normalizedPath = downloadPath.endsWith("/") ? downloadPath : downloadPath + "/";
    return normalizedPath + getModelFileName();
  }

  private String getModelFileName() {

    try {
      java.net.URL urlObj = new java.net.URL(modelURL);
      String path = urlObj.getPath();
      String fileName = path.substring(path.lastIndexOf('/') + 1);

      if (fileName.isEmpty()) {
        throw new IllegalArgumentException("No filename found in URL: " + modelURL);
      }

      return fileName;
    } catch (java.net.MalformedURLException e) {
      throw new IllegalArgumentException("Invalid URL: " + modelURL, e);
    }
  }
}
