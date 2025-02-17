package com.mule.whisperer.internal.metadata;

import com.mule.whisperer.api.OpenAiTranscriptionAttributes;
import com.mule.whisperer.api.STTParamsModelDetails;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.AttributesTypeResolver;
import org.mule.runtime.api.metadata.resolving.FailureCode;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

import java.util.Collections;
import java.util.Set;

public class TranscriptionOutputResolver implements TypeKeysResolver, OutputTypeResolver<STTParamsModelDetails>, AttributesTypeResolver<STTParamsModelDetails> {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, STTParamsModelDetails sttParamsModelDetails) throws MetadataResolvingException, ConnectionException {
        return metadataContext.getTypeBuilder().stringType().build();
    }

    @Override
    public MetadataType getAttributesType(MetadataContext metadataContext, STTParamsModelDetails params) throws MetadataResolvingException, ConnectionException {
        if (params.isVerbose()) {
            return metadataContext.getTypeLoader().load(OpenAiTranscriptionAttributes.class);
        } else {
            return metadataContext.getTypeBuilder().nullType().build();
        }
    }

    @Override
    public Set<MetadataKey> getKeys(MetadataContext metadataContext) throws MetadataResolvingException, ConnectionException {
        return Collections.emptySet();
    }

    @Override
    public String getResolverName() {
        return TypeKeysResolver.super.getResolverName();
    }

    @Override
    public String getCategoryName() {
        return "Whisper";
    }
}
