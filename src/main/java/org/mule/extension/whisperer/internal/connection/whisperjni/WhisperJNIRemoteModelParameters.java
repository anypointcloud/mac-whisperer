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
  private String url;

  @Parameter
  @DisplayName("Download path")
  @Expression(ExpressionSupport.SUPPORTED)
  @Placement(order = 2)
  @Example("mule.home ++ \"/apps/\" ++ app.name ++ \"/\"")
  private String downloadPath;

  private String modelFilePath;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public void setDownloadPath(String downloadPath) {
    this.downloadPath = downloadPath;
  }

  public String getModelFilePath() {
    return modelFilePath;
  }
}
