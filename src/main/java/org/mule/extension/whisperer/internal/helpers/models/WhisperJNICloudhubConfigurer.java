package org.mule.extension.whisperer.internal.helpers.models;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Enumeration;

public class WhisperJNICloudhubConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(WhisperJNICloudhubConfigurer.class);

  public static final String WHISPER_DEPENDENCY_RESOURCE_PATH = "/cloudhub-whisper-dependencies.zip";
  public static final String WHISPER_DEPENDENCY_LIBS_PATH = "/tmp/whisper-deps-linux64";

  public static boolean isCloudHubDeployment() {
    // Check if the system property cloudhub.deployment is set to true
    return Boolean.getBoolean("cloudhub.deployment");
  }

  public static void setup() {

    // CloudHub specific setup can be done here

    try {

      LOGGER.info("Setting up Whisper JNI for CloudHub deployment. io.github.givimad.whisperjni.libdir={}", WHISPER_DEPENDENCY_LIBS_PATH);
      // Set the system property for the Whisper JNI library directory
      System.setProperty("io.github.givimad.whisperjni.libdir", WHISPER_DEPENDENCY_LIBS_PATH);
      // Extract the Whisper JNI dependencies
      LOGGER.info("Extracting Whisper JNI dependencies from JAR resource.");
      extractSoLibs();

    } catch (Exception e) {
      LOGGER.error("Error in Whisper setup", e);
    }
  }

  private static void extractSoLibs() throws IOException {
    // Extract JAR resource to a temp file
    File tempZip = File.createTempFile("whisper-dependencies", ".zip", new File("/tmp"));

    LOGGER.info("Extracting {}.", WHISPER_DEPENDENCY_RESOURCE_PATH);

    try (InputStream zipStream = WhisperJNICloudhubConfigurer.class.getResourceAsStream(WHISPER_DEPENDENCY_RESOURCE_PATH);
        OutputStream outStream = new FileOutputStream(tempZip)) {
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = zipStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, bytesRead);
      }
    }

    // Create the destination directory if it doesn't exist
    File destDir = new File(WHISPER_DEPENDENCY_LIBS_PATH);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }

    try (ZipFile zipFile = new ZipFile(tempZip)) {
      for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
        ZipArchiveEntry entry = entries.nextElement();
        // Get just the file name without path
        String fileName = new File(entry.getName()).getName();
        // Create a new entry with just the filename
        ZipArchiveEntry newEntry = new ZipArchiveEntry(fileName);
        newEntry.setUnixMode(entry.getUnixMode());
        // Extract using the simplified entry
        LOGGER.info("Extracting Whisper JNI dependency {}", fileName);
        extractZipEntry(zipFile, entry, destDir, fileName);
      }
    }

    tempZip.delete();
  }

  private static void extractZipEntry(ZipFile zipFile, ZipArchiveEntry entry, File destDir, String fileName) throws IOException {
    File outputFile = new File(destDir, fileName);

    if (!entry.isDirectory()) {
      // Handle symlinks
      if (entry.isUnixSymlink()) {
        String targetPath = zipFile.getUnixSymlink(entry); // Get symlink target
        // Use just the filename part of the target
        String targetFileName = new File(targetPath).getName();
        Path target = Paths.get(destDir.getAbsolutePath(), targetFileName);
        Files.createSymbolicLink(outputFile.toPath(), target);
      } else {
        Files.copy(zipFile.getInputStream(entry), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.setPosixFilePermissions(outputFile.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
      }

      // Set executable permissions if applicable
      if ((entry.getUnixMode() & 0b001000000) != 0) { // Check Unix executable bit
        outputFile.setExecutable(true);
      }
    }
  }
}
