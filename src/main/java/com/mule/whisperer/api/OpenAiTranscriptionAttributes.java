package com.mule.whisperer.api;

public class OpenAiTranscriptionAttributes {
    private String language;
    private Double duration;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
