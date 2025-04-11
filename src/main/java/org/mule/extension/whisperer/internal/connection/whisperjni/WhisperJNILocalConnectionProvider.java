package org.mule.extension.whisperer.internal.connection.whisperjni;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperJNI;
import org.mule.extension.whisperer.internal.helpers.models.WhisperJNICloudhubConfigurer;
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
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Alias("whisperjnifile")
@DisplayName("Whisper JNI (Local .bin)")
public class WhisperJNILocalConnectionProvider implements CachedConnectionProvider<WhisperJNIConnection>, Startable, Stoppable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhisperJNILocalConnectionProvider.class);

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "4")
    private int threads;

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "false")
    private boolean translate;

    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Optional(defaultValue = "false")
    private boolean printProgress;

    @ParameterGroup(name ="Model")
    @Expression(ExpressionSupport.SUPPORTED)
    private WhisperJNILocalModelParameters model;

    private WhisperJNI whisper;
    private WhisperContext whisperContext;
    @Override
    public WhisperJNIConnection connect() throws ConnectionException {
        return new WhisperJNIConnection(whisper, whisperContext, threads, translate, printProgress);
    }

    @Override
    public void disconnect(WhisperJNIConnection whisperJNIConnection) {
    }

    @Override
    public ConnectionValidationResult validate(WhisperJNIConnection whisperJNIConnection) {
        return ConnectionValidationResult.success();
    }

    @Override
    public void start() throws MuleException {
        try {

            if(WhisperJNICloudhubConfigurer.isCloudHubDeployment()) {
                LOGGER.info("CloudHub deployment detected. Performing CloudHub specific setup.");
                Path dependenciesPath = Paths.get(WhisperJNICloudhubConfigurer.WHISPER_DEPENDENCY_LIBS_PATH);
                if(!Files.exists(dependenciesPath)) {
                    synchronized (WhisperJNIRemoteConnectionProvider.class) {
                        if (!Files.exists(dependenciesPath)) {
                            WhisperJNICloudhubConfigurer.setup();;
                        }
                    }
                }
            }

            WhisperJNI.loadLibrary();
            whisper = new WhisperJNI();
            whisperContext = whisper.init(Paths.get(model.getModelFilePath()));

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
