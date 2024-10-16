package com.sinian;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        ConfigFileManager configFileManager = new ConfigFileManager();

        if (!configFileManager.isConfigFileExists()) {
            System.out.println("Configuration file not found. Please provide the necessary configuration data.");
            ConfigInputHandler configInputHandler = new ConfigInputHandler();
            Properties properties = configInputHandler.collectConfigData();
            configFileManager.createConfigFile(properties);
            System.out.println("Configuration saved successfully.");

            configInputHandler.close();
            System.out.println("Please, restart the program.");
            System.exit(0);

        } else {
            System.out.println("Configuration file already exists. No need to reconfigure.");
        }
        // Capture image
        String capturedImagePath = CaptureImage.captureImage();
        if (capturedImagePath == null) {
            System.out.println("Failed to capture image.");
            return;
        }
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