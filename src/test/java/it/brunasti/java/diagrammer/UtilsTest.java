package it.brunasti.java.diagrammer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest implements TestConstants {

  // Test loadConfigurationFile function ---------------------------
  @Test
  @DisplayName("Read the configuration file with the correct content")
  void testLoadConfigurationFile_Successful() {
    JSONObject jsonObject = Utils.loadConfigurationFile(configurationFileName);
    assertNotNull(jsonObject);

    JSONObject exclude = (JSONObject) jsonObject.get("exclude");
    assertNotNull(exclude);

    JSONArray classes = (JSONArray) exclude.get("classes");
    assertNotNull(classes);

    JSONArray packages = (JSONArray) exclude.get("packages");
    assertNotNull(packages);
  }

  @Test
  @DisplayName("Read the configuration file with the wrong content")
  void testLoadConfigurationFile_WrongJSON() {
    JSONObject jsonObject = Utils.loadConfigurationFile(wrongJsonFileName);
    assertNotNull(jsonObject);

    JSONObject exclude = (JSONObject) jsonObject.get("exclude");
    assertNotNull(exclude);

    JSONArray classes = (JSONArray) exclude.get("classes");
    assertNull(classes);
  }

  @Test
  @DisplayName("Read the wrong configuration file that doesn't exist or is not json")
  void testLoadConfigurationFile_Fail() {
    JSONObject jsonObject = Utils.loadConfigurationFile(configurationFileName + ".error");
    assertNull(jsonObject);

    jsonObject = Utils.loadConfigurationFile(wrongFileName);
    assertNull(jsonObject);
  }


  // Test listDirectories function ---------------------------
  @Test
  @DisplayName("List Temp directory")
  void testlistDirectories_Temp() {
    assertDoesNotThrow(() -> Utils.listDirectories(tempDirectory));
    try {
      Set<String> list = Utils.listDirectories(tempDirectory);
      assertNotNull(list);
      assertEquals(0,list.size());
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  @Test
  @DisplayName("List Src directory")
  void testlistDirectories_Src() {
    assertDoesNotThrow(() -> Utils.listDirectories(srcDirectory));
    try {
      Set<String> list = Utils.listDirectories(srcDirectory);
      assertNotNull(list);
      assertEquals(2,list.size());
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  @Test
  @DisplayName("List non existing directory")
  void testlistDirectories_NonExisting() {
    Throwable exception = assertThrows(NoSuchFileException.class, () -> Utils.listDirectories(nonExistingDirectory));
    assertEquals("./errorDir", exception.getMessage());
  }


  // Test listFilesUsingFilesList function ---------------------------
  @Test
  @DisplayName("List Files in temp directory")
  void testListFilesUsingFilesList_Temp() {
    assertDoesNotThrow(() -> Utils.listFilesUsingFilesList(tempDirectory));
    try {
      Set<String> list = Utils.listFilesUsingFilesList(tempDirectory);
      assertNotNull(list);
      assertEquals(4,list.size());
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  @Test
  @DisplayName("List Files in Src directory")
  void testListFilesUsingFilesList_Src() {
    assertDoesNotThrow(() -> Utils.listFilesUsingFilesList(srcDirectory));
    try {
      Set<String> list = Utils.listFilesUsingFilesList(srcDirectory);
      assertNotNull(list);
      assertEquals(0,list.size());
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  @Test
  @DisplayName("List Files in non existing directory")
  void testListFilesUsingFilesList_NonExisting() {
    Throwable exception = assertThrows(NoSuchFileException.class, () -> Utils.listFilesUsingFilesList(nonExistingDirectory));
    assertEquals("./errorDir", exception.getMessage());
  }


}