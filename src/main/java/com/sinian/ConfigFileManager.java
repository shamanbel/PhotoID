package com.sinian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileManager {
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    //"src/main/resources/config.properties"
    // Метод для проверки существования файла конфигурации
    public boolean isConfigFileExists() {
        File configFile = new File(CONFIG_FILE_PATH);
        return configFile.exists();
    }

    // Метод для создания и сохранения файла конфигурации
    public void createConfigFile(Properties properties) {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(out, "User Configuration");
            System.out.println("Configuration file created successfully at: " + CONFIG_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Properties readConfigFile() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}