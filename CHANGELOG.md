# Changelog

All notable changes to the MuleSoft Whisperer Connector will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2025-10-20

### Changed
- **BREAKING**: Removed JCodec dependency due to non-functional Transcoder API for AAC/M4A conversion
- M4A/AAC format support now requires optional ByteDeco FFmpeg dependency
- Migrated audio conversion architecture to ByteDeco FFmpeg for extended formats (M4A/AAC/FLAC/OGG/WEBM)
- Core formats (MP3, WAV) remain pure Java with no additional dependencies

### Added
- Full ByteDeco FFmpeg implementation using Java API (not command-line)
- Comprehensive test suite covering all 7 supported audio formats
- Clear error messages guiding users to add ByteDeco dependency when needed

### Removed
- JCodec dependency (proven non-functional for AAC→WAV conversion after extensive diagnostic testing)
- JCodecConverter class and related diagnostic utilities

### Fixed
- M4A/AAC audio conversion now works correctly via ByteDeco FFmpeg
- All audio format conversions produce proper 16kHz mono WAV output as required by WhisperJNI

### Migration Guide

If you use M4A/AAC/FLAC/OGG/WEBM formats, add ByteDeco FFmpeg to your Mule application and configure it as a shared library:

```xml
<dependencies>
    <!-- ByteDeco FFmpeg: All platforms (~150MB) -->
    <dependency>
        <groupId>org.bytedeco</groupId>
        <artifactId>ffmpeg-platform</artifactId>
        <version>6.1.1-1.5.10</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.mule.tools.maven</groupId>
            <artifactId>mule-maven-plugin</artifactId>
            <version>4.3.0</version>
            <extensions>true</extensions>
            <configuration>
                <sharedLibraries>
                    <sharedLibrary>
                        <groupId>org.bytedeco</groupId>
                        <artifactId>ffmpeg</artifactId>
                    </sharedLibrary>
                    <sharedLibrary>
                        <groupId>org.bytedeco</groupId>
                        <artifactId>javacpp</artifactId>
                    </sharedLibrary>
                </sharedLibraries>
            </configuration>
        </plugin>
    </plugins>
</build>
```

MP3 and WAV formats continue to work without any additional dependencies.

## [0.3.0] - Previous Release

### Added
- Initial speech-to-text (STT) support via Whisper JNI
- Initial text-to-speech (TTS) support
- Audio format conversion for MP3, M4A, WAV using JCodec and JLayer
