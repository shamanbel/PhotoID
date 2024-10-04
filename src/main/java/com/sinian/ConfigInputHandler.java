package com.sinian;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class ConfigInputHandler {  private final Scanner scanner;
    static {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public ConfigInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public Properties collectConfigData() {
        Properties properties = new Properties();



        // String sendGridApiKey = promptAndValidateInput("Enter SendGrid API Key: ", this::isNotEmpty);
       // properties.setProperty("sendgrid.api.key", sendGridApiKey);


     /*   String fromEmail = promptAndValidateInput("Enter From Email: ", this::isValidEmail);
        properties.setProperty("from.email", fromEmail);*/


        String toEmail = promptAndValidateInput("Enter To Email: ", this::isValidEmail);
        properties.setProperty("to.email", toEmail);


     /*   String maxRetries = promptAndValidateInput("Enter Max Retries: ", this::isNumeric);
        properties.setProperty("max.retries", maxRetries);*/


/*        String retryDelay = promptAndValidateInput("Enter Retry Delay (minutes): ", this::isNumeric);
        properties.setProperty("retry.delay.minute", retryDelay);*/

        String similarityThreshold = promptAndValidateInput("Enter Similarity Threshold (0-1): ", this::isFloatBetweenZeroAndOne);
        properties.setProperty("similarity.threshold", similarityThreshold);


        String allowedImagePath = captureImageWithConfirmation("src/main/resources/allowed_images");

        if (allowedImagePath != null) {
            properties.setProperty("allowed.image.path", allowedImagePath);
        } else {
            System.out.println("Failed to capture image.");
            System.exit(1);
        }

        return properties;
    }


    // Generic method for requesting input and validating data
    private String promptAndValidateInput(String promptMessage, ValidationRule validationRule) {
        String input;
        do {
            System.out.print(promptMessage);
            input = scanner.nextLine();
            if (!validationRule.validate(input)) {
                System.out.println("Invalid input. Please try again.");
            }
        } while (!validationRule.validate(input));
        return input;
    }


    // Define an interface for validation rules
    private interface ValidationRule {
        boolean validate(String input);
    }

    // Метод для захвата изображения с предупреждением, задержкой и подтверждением
    public String captureImageWithConfirmation(String directoryPath) {
        String imagePath = null;

        // Предупреждение о съемке
        System.out.println("The camera will be used to capture an image of the owner.");
        System.out.println("Please be ready, the capture will start in 5 seconds...");

        try {

            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isApproved = false;


        while (!isApproved) {
            imagePath = captureImage(directoryPath);

            if (imagePath != null) {

                displayImage(imagePath);
                System.out.println("Is this photo acceptable for comparison? (yes/no): ");
                String answer = scanner.nextLine().toLowerCase();

                if (answer.equals("yes")) {
                    isApproved = true; // Если фото подходит, завершаем цикл
                    System.out.println("Image saved as " + imagePath);
                } else {
                    deleteFile(imagePath);
                    System.out.println("Retrying the capture process...");
                }
            } else {
                System.out.println("Error during capturing the image. Retrying...");
            }
        }

        return imagePath;
    }


    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("Deleted the file: " + filePath);
        } else {
            System.out.println("Failed to delete the file: " + filePath);
        }
    }

    public static void displayImage(String imagePath) {
        try {
            Image img = ImageIO.read(new File(imagePath));
            ImageIcon icon = new ImageIcon(img);
            JFrame frame = new JFrame();
            frame.setLayout(new FlowLayout());
            frame.setSize(500, 500);
            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException e) {
            System.out.println("Error: Could not display image");
        }
    }

    public static String captureImage(String directoryPath) {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not available");
            return null;
        }

        Mat frame = new Mat();
        if (camera.read(frame)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "capture_" + timeStamp + ".png";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                System.out.println("Failed to create directory: " + directoryPath);
                return null;
            }

            String filePath = directoryPath + File.separator + filename;
            Imgcodecs.imwrite(filePath, frame);
            camera.release();
            return filePath;
        } else {
            System.out.println("Error: Cannot capture image");
            camera.release();
            return null;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

   private boolean isFloatBetweenZeroAndOne(String str) {
        try {
            float value = Float.parseFloat(str);
            return value >= 0.0f && value <= 1.0f;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void close() {
        scanner.close();
    }
}