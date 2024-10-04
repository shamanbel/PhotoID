package com.sinian;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.*;
import java.util.Properties;

public class ImageCompare {

    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    private static double SIMILARITY_THRESHOLD;
    private static String allowedImagePath;

    static {
        // Load properties from the configuration file
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) { // Используем FileInputStream для загрузки файла с файловой системы

            properties.load(input);
            allowedImagePath = properties.getProperty("allowed.image.path");
            System.out.println("allowedImagePath " + allowedImagePath);
            SIMILARITY_THRESHOLD = Double.parseDouble(properties.getProperty("similarity.threshold"));
        } catch (Exception e) {
            System.out.println("Error loading " + CONFIG_FILE + ": " + e.getMessage());
        }

    }
    // Метод для сравнения изображения пользователя с единственным эталонным изображением
    // Method to compare user image with single reference image
    public static boolean compareWithAllowedImage(String capturedImagePath) {

        // Загружаем изображение пользователя
        // Load the user's image
        Mat userImage = Imgcodecs.imread(capturedImagePath);
        if (userImage.empty()) {
            System.out.println("Error: User image not found");
            return false;
        }

        // Загружаем эталонное изображение
        // Load the reference image
        Mat allowedImage = Imgcodecs.imread(allowedImagePath);
        if (allowedImage.empty()) {
            System.out.println("Error: Allowed image not found");
            return false;
        }
        // Выделяем лица на обоих изображениях
        // Select faces in both images
        Mat userFace = detectAndExtractFace(userImage);
        Mat allowedFace = detectAndExtractFace(allowedImage);
        if (userFace == null || allowedFace == null) {
            System.out.println("Face detection failed.");
            return false;
        }
        // Сравниваем изображения лиц
        // Compare face images
        return compareImages(userFace, allowedFace);
    }

    // Метод для сравнения двух изображений
    // Method for comparing two images
    private static boolean compareImages(Mat img1, Mat img2) {
        // Преобразуем изображения в оттенки серого
        // Convert images to grayscale
        Mat grayImg1 = new Mat();
        Mat grayImg2 = new Mat();
        Imgproc.cvtColor(img1, grayImg1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(img2, grayImg2, Imgproc.COLOR_BGR2GRAY);

        // Вычисляем гистограмму изображений
        // Calculate the histogram of images
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        Imgproc.calcHist(java.util.Collections.singletonList(grayImg1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0f, 256f));
        Imgproc.calcHist(java.util.Collections.singletonList(grayImg2), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0f, 256f));

        // Сравниваем гистограммы
        // Compare histograms
        double similarity = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
        System.out.println("Similarity: " + similarity);
        // Если схожесть больше порогового значения, изображения считаются похожими
        // If the similarity is greater than the threshold, the images are considered similar
        return similarity > SIMILARITY_THRESHOLD;
    }
    // Метод для обнаружения и выделения лица с изображения
    // Method for detecting and extracting a face from an image
    private static Mat detectAndExtractFace(Mat image) {
        // Путь к файлу каскада в ресурсах
        String faceCascadePath = "/haarcascade_frontalface_default.xml"; // Путь относительно корня ресурсов

        CascadeClassifier faceDetector = new CascadeClassifier();

        try (InputStream cascadeStream = ImageCompare.class.getResourceAsStream(faceCascadePath)) {
            if (cascadeStream == null) {
                System.out.println("Cascade file not found in resources!");
                return null;
            }

            // Создание временного файла для каскада
            File tempFile = File.createTempFile("cascade", ".xml");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cascadeStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Инициализация CascadeClassifier с использованием временного файла
            faceDetector.load(tempFile.getAbsolutePath());
            tempFile.deleteOnExit(); // Удаление временного файла при выходе
        } catch (IOException e) {
            System.out.println("Error loading cascade file: " + e.getMessage());
            return null;
        }

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        if (faceDetections.toArray().length == 0) {
            System.out.println("No faces detected.");
            return null;
        }

        // Берем первое обнаруженное лицо
        Rect faceRect = faceDetections.toArray()[0];

        // Возвращаем изображение обрезанного лица
        return new Mat(image, faceRect);
    }

/*    private static Mat detectAndExtractFace(Mat image) {
        String faceCascadePath = "src/main/resources/haarcascade_frontalface_default.xml";

        CascadeClassifier faceDetector = new CascadeClassifier(faceCascadePath);

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        if (faceDetections.toArray().length == 0) {
            System.out.println("No faces detected.");
            return null;
        }

        // Берем первое обнаруженное лицо
        // Take the first detected face
        Rect faceRect = faceDetections.toArray()[0];

        // Return the cropped face image
        return new Mat(image, faceRect);
    }*/
}
