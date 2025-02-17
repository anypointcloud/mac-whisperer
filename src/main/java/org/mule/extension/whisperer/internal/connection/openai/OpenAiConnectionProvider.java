package org.mule.extension.whisperer.internal.connection.openai;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.tls.TlsContextFactory;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

@Alias("open-ai")
public class OpenAiConnectionProvider implements CachedConnectionProvider<OpenAiConnection>, Startable, Stoppable {
    private static final String API_URL = "https://api.openai.com/v1/";

    @Parameter
    private String apiKey;

    private HttpClient httpClient;
    @RefName
    private String configName;

    @Inject
    private HttpService httpService;

    @Parameter
    @Optional
    private TlsContextFactory tlsContext;

    @Override
    public OpenAiConnection connect() throws ConnectionException {
        try {
            return new OpenAiConnection(apiKey, httpClient, new URI(API_URL));
        } catch (URISyntaxException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void disconnect(OpenAiConnection openAiConnection) {
    }

    @Override
    public ConnectionValidationResult validate(OpenAiConnection openAiConnection) {
        try {
            openAiConnection.validate();
            return ConnectionValidationResult.success();
        }
        catch (ConnectionException e) {
            return ConnectionValidationResult.failure(e.getMessage(), e);
        }
    }

    @Override
    public void start() throws MuleException {
        HttpClientConfiguration config = createClientConfiguration();
        httpClient = httpService.getClientFactory().create(config);
        httpClient.start();
    }

    private HttpClientConfiguration createClientConfiguration() {
        HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder()
                .setName(configName);
        // TODO: support proxy https://docs.mulesoft.com/mule-sdk/latest/HTTP-based-connectors
        if (null != tlsContext) {
            builder.setTlsContextFactory(tlsContext);
        } else {
            builder.setTlsContextFactory(TlsContextFactory.builder().buildDefault());
        }
        return builder.build();
    }

    @Override
    public void stop() throws MuleException {
        if (httpClient != null) {
            httpClient.stop();
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
