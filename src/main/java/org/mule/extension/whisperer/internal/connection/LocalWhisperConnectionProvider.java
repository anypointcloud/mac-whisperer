package org.mule.extension.whisperer.internal.connection;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperJNI;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.core.api.lifecycle.StartException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.IOException;
import java.nio.file.Paths;

@Alias("local-whisper")
public class LocalWhisperConnectionProvider implements CachedConnectionProvider<LocalWhisperConnection>, Startable, Stoppable {

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "4")
    private int threads;

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "en")
    private String language;

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "false")
    private boolean translate;

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    private String modelPath;

    private WhisperJNI whisper;
    private WhisperContext whisperContext;
    @Override
    public LocalWhisperConnection connect() throws ConnectionException {
        return new LocalWhisperConnection(whisper, whisperContext, threads, language);
    }

    @Override
    public void disconnect(LocalWhisperConnection localWhisperConnection) {
    }

    @Override
    public ConnectionValidationResult validate(LocalWhisperConnection localWhisperConnection) {
        return ConnectionValidationResult.success();
    }

    @Override
    public void start() throws MuleException {
        try {
            WhisperJNI.loadLibrary();
            whisper = new WhisperJNI();
            whisperContext = whisper.init(Paths.get(modelPath));
        } catch (IOException e) {
            throw new StartException(e, this);
        }
    }

    @Override
    public void stop() throws MuleException {
        if (null != whisperContext) {
            whisperContext.close();
        }
    }
}
