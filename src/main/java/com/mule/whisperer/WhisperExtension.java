package com.mule.whisperer;

import com.mule.whisperer.internal.connection.OpenAiConnectionProvider;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;



@Xml(prefix = "whisperer")
@Extension(name = "MAC Whisperer")
@Configurations(WhisperConfiguration.class)
@ConnectionProviders(OpenAiConnectionProvider.class)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class WhisperExtension {

}
