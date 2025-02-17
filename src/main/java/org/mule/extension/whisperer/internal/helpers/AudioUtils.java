package org.mule.extension.whisperer.internal.helpers;

import org.mule.runtime.api.metadata.MediaType;

public class AudioUtils {
    public static boolean isWav(MediaType mediaType) {
        switch (mediaType.withoutParameters().toString()) {
            case "audio/wav":
            case "audio/vnd.wav":
            case "audio/vnd.wave":
            case "audio/wave":
            case "audio/x-wav":
            case "audio/x-pn-wav":
                return true;
        }
        return false;
    }
    public static String guessAudioFileExtension(MediaType mediaType) {
        String extension = "mp3";
        switch (mediaType.withoutParameters().toString()) {
            case "audio/m4a":
            case "audio/mp4":
                extension = "m4a";
                break;
            case "audio/flac":
            case "audio/x-flac":
                extension = "flac";
                break;
            case "audio/wav":
            case "audio/vnd.wav":
            case "audio/vnd.wave":
            case "audio/wave":
            case "audio/x-wav":
            case "audio/x-pn-wav":
                extension = "wav";
                break;
            case "audio/ogg":
                extension = "ogg";
                break;
            case "audio/webm":
                extension = "weba";
                break;
            case "audio/aac":
                extension = "aac";
                break;
        }
        return extension;
    }
}
