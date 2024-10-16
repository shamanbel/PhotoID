package com.sinian;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureImage {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Method for capturing image from camera
    public static String captureImage() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not available");
            return null;
        }

        Mat frame = new Mat();
        if (camera.read(frame)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "capture_" + timeStamp + ".png";
            String directoryPath = "src/main/resources/captured_images";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                System.out.println("Failed to create directory: " + directoryPath);
                return null;
            }

            String filePath = directoryPath + File.separator + filename;
            Imgcodecs.imwrite(filePath, frame);
            System.out.println("Image captured and saved as " + filePath);
            camera.release();
            return filePath;
        } else {
            System.out.println("Error: Cannot capture image");
            camera.release();
            return null;
        }
    }
}



