package com.mule.whisperer.internal.connection;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import com.mule.whisperer.internal.error.ConnectionIncompatibleException;
import com.mule.whisperer.internal.error.TranscriptionException;
import com.mule.whisperer.internal.helpers.AudioFileReader;
import com.mule.whisperer.internal.helpers.AudioUtils;
import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.concurrent.CompletableFuture;

public class LocalWhisperConnection implements WhisperConnection {
    private static Logger LOGGER = LoggerFactory.getLogger(LocalWhisperConnection.class);

    private final WhisperJNI whisper;
    private final WhisperContext whisperContext;
    private final int threads;
    private final String language;
    public LocalWhisperConnection(WhisperJNI whisper, WhisperContext whisperContext, int threads, String language) {
        this.whisper = whisper;
        this.whisperContext = whisperContext;
        this.threads = threads;
        this.language = language;
    }
    @Override
    public CompletableFuture<Result<String, Object>> transcribe(TypedValue<InputStream> audioContent, String fineTuningPrompt, STTParamsModelDetails params) {
        WhisperFullParams whisperParams = new WhisperFullParams();
        whisperParams.nThreads = threads;
        whisperParams.language = language;
        whisperParams.translate = false;
        whisperParams.printProgress = false;

        LOGGER.debug("Whisper context initialized successfully. Processing audio input.");

        // Process the InputStream based on the audio format
        String appHomePath = System.getProperty("app.home");
        File tempAudioFile = new File(appHomePath, "audio." + AudioUtils.guessAudioFileExtension(audioContent.getDataType().getMediaType()));
        try (OutputStream outStream = new FileOutputStream(tempAudioFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audioContent.getValue().read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return CompletableFuture.supplyAsync(() -> {
                throw new TranscriptionException("Unable to buffer audio data for transcription", e);
            });
        }
        LOGGER.trace("Audio file successfully saved to temporary file: {}", tempAudioFile.getAbsolutePath());

        // Convert audio to WAV if needed
        String processedFilePath = tempAudioFile.getAbsolutePath();
        if (! AudioUtils.isWav(audioContent.getDataType().getMediaType())) {
            LOGGER.trace("Converting audio file to WAV format.");
            String wavFilePath = processedFilePath.replaceAll("\\.\\w+$", ".wav");
            try {
                AudioFileReader.convertMp3ToWav(processedFilePath, wavFilePath);
            } catch (UnsupportedAudioFileException e) {
                return CompletableFuture.supplyAsync(() -> {
                    throw new TranscriptionException("Unable to convert " + audioContent.getDataType().getMediaType().toString() + " to wav format", e);
                });
            } catch (IOException e) {
                return CompletableFuture.supplyAsync(() -> {
                    throw new TranscriptionException("Error converting audio content to wav format", e);
                });
            }
            processedFilePath = wavFilePath;
            LOGGER.debug("Audio file converted to WAV format: {}", wavFilePath);
        }

        // Read the audio file and get the audio samples
        LOGGER.trace("Reading audio file and extracting samples.");
        try {
            float [] samples = AudioFileReader.readFile(new File(processedFilePath));

            // Perform the speech-to-text operation
            LOGGER.debug("Performing speech-to-text operation with local Whisper.");
            int result = whisper.full(whisperContext, whisperParams, samples, samples.length);

            if (result != 0) {
                return CompletableFuture.supplyAsync(() -> {
                   throw new TranscriptionException("Transcription failed with code " + result);
                });
            }

            int segments = whisper.fullNSegments(whisperContext);
            // Collect the transcribed text from all segments
            // TODO: add segments attribute similar to OpenAI verbose
            StringBuilder transcription = new StringBuilder();
            for (int i = 0; i < segments; ++i) {
                transcription.append(whisper.fullGetSegmentText(whisperContext, i)).append(" ");
            }
            return CompletableFuture.supplyAsync(() -> Result.<String, Object>builder()
                    .output(transcription.toString())
                    .build());
        } catch (UnsupportedAudioFileException | IOException e) {
            return CompletableFuture.supplyAsync(() -> {
                throw new TranscriptionException(e);
            });
        }
    }

    @Override
    public CompletableFuture<InputStream> generate(String text, TTSParamsModelDetails params) {
        return CompletableFuture.supplyAsync(() -> {
            throw new ConnectionIncompatibleException("Operation not supported by Local Whisper connection");
        });
    }

    public WhisperContext getWhisperContext() {
        return whisperContext;
    }
}
