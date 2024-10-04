package com.sinian;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Main {
    static {
        // System.loadLibrary("C:\\Java\\opencv\\build\\java\\x64\\opencv_java470.dll");
        System.loadLibrary("opencv_java470");
    }

    public static void main(String[] args) {
        ConfigFileManager configFileManager = new ConfigFileManager();

        if (!configFileManager.isConfigFileExists()) {
            System.out.println("Configuration file not found. Please provide the necessary configuration data.");

            // Создаем объект ConfigInputHandler для сбора и проверки данных
            ConfigInputHandler configInputHandler = new ConfigInputHandler();

            // Сбор и проверка данных
            Properties properties = configInputHandler.collectConfigData();

            // Сохраняем конфигурацию в файл через ConfigFileManager
            configFileManager.createConfigFile(properties);
            System.out.println("Configuration saved successfully.");

            // Закрываем ресурсы
            configInputHandler.close();
            System.out.println("Please, restart the program.");
            System.exit(0);

        } else {
            System.out.println("Configuration file already exists. No need to reconfigure.");
        }

        // Захват изображения
        // Capture image
        String capturedImagePath = CaptureImage.captureImage();
        if (capturedImagePath == null) {
            System.out.println("Failed to capture image.");
            return;
        }

        // Сравнение захваченного изображения с изображением разрешенного пользователя
        // Compare the captured image with the authorized user's image
        boolean isAuthorized = ImageCompare.compareWithAllowedImage(capturedImagePath);

        if (!isAuthorized) {
           try {
                EmailSender.sendEmailWithAttachment(capturedImagePath);
                System.out.println("Email sent successfully.");
            } catch (IOException e) {
                System.out.println("Error sending email: " + e.getMessage());
            }
        } else {
            // Удаление изображения, если аутентификация успешна
            // Remove image if authentication is successful
            File capturedImageFile = new File(capturedImagePath);
            if (capturedImageFile.delete()) {
                System.out.println("Captured image deleted successfully.");
            } else {
                System.out.println("Failed to delete captured image.");
            }
        }
    }
}