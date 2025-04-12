# <img src="icon/icon.svg" width="6%" alt="banner"> MuleSoft  Whisperer Connector
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mulesoft-ai-chain-project/mule4-whisperer-connector)](https://central.sonatype.com/artifact/io.github.mulesoft-ai-chain-project/mule4-whisperer-connector/overview)

## <img src="https://raw.githubusercontent.com/MuleSoft-AI-Chain-Project/.github/main/profile/assets/mulechain-project-logo.png" width="6%" alt="banner">   [MuleSoft AI Chain (MAC) Project](https://mac-project.ai/docs/)

### <img src="icon/icon.svg" width="6%" alt="banner">   [MuleSoft Whisperer Connector](https://mac-project.ai/docs/mac-whisperer/connector-overview)

MAC Whisperer supports 2 main use cases, 
- **Speech-to-Text**: Converts audio files (wav, mp3, etc.) into text
- **Text-to-Speech**: Converts text to audio files (wav, mp3, etc.)

### Requirements

#### Mule Runtime
Mulesoft Runtime >= 4.9.0

#### JDK

- The  supported version for Java SDK is JDK 17.
- Compilation with Java SDK must be done with JDK 17.

### Installation (using maven central dependency)

```xml
<dependency>
   <groupId>io.github.mulesoft-ai-chain-project</groupId>
   <artifactId>mule4-whisperer-connector</artifactId>
   <version>{version}</version>
   <classifier>mule-plugin</classifier>
</dependency>
```

### Installation (building locally)

To use this connector, first [build and install](https://mac-project.ai/docs/mac-whisperer/getting-started) the connector into your local maven repository.
Then add the following dependency to your application's `pom.xml`:

```xml
<dependency>
    <groupId>com.mulesoft.connectors</groupId>
    <artifactId>mule4-whisperer-connector</artifactId>
    <version>{version}</version>
    <classifier>mule-plugin</classifier>
</dependency>
```

### Installation into private Anypoint Exchange

You can also make this connector available as an asset in your Anyooint Exchange.

This process will require you to build the connector as above, but additionally you will need
to make some changes to the `pom.xml`.  For this reason, we recommend you fork the repository.

Then, follow the MuleSoft [documentation](https://docs.mulesoft.com/exchange/to-publish-assets-maven) to modify and publish the asset.

### Contribution
[How to contribute](https://mac-project.ai/docs/contribute)

### Documentation
- Check out the complete documentation in [mac-project.ai](https://mac-project.ai/docs/mac-whisperer/connector-overview)

---

### Stay tuned!

- 🌐 **Website**: [mac-project.ai](https://mac-project.ai)
- 📺 **YouTube**: [@MuleSoft-MAC-Project](https://www.youtube.com/@MuleSoft-MAC-Project)
- 💼 **LinkedIn**: [MAC Project Group](https://lnkd.in/gW3eZrbF)
