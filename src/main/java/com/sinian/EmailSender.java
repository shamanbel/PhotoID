package com.sinian;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class EmailSender {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    private static String SENDGRID_API_KEY;
    private static String FROM_EMAIL;
    private static String TO_EMAIL;
    private static int MAX_RETRIES;
    private static long RETRY_DELAY_M;


    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.load(input);
            SENDGRID_API_KEY = properties.getProperty("sendgrid.api.key", "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"); // Дефолтное значение
            FROM_EMAIL = properties.getProperty("from.email", "vladimirsinianski@gmail.com");
            TO_EMAIL = properties.getProperty("to.email");
            MAX_RETRIES = Integer.parseInt(properties.getProperty("max.retries","10"));
            RETRY_DELAY_M = Long.parseLong(properties.getProperty("retry.delay.minute", "5000"));
        } catch (IOException ex) {

            System.exit(1);
        }
    }

    public static void sendEmailWithAttachment(String filePath) throws IOException {
        // Получаем IP-адрес пользователя
        // Get the user's IP address
        String publicIP = IPFetcher.getPublicIP();
        System.out.println("Public IP Address: " + publicIP);

        Email from = new Email(FROM_EMAIL);
        String subject = "Unauthorized Access Detected";
        Email to = new Email(TO_EMAIL);
        String emailBody = "An unauthorized access attempt was detected.\n\n" +
                "Public IP Address: " + publicIP + "\n\n" +
                "See the attached photo for more details.";
        Content content = new Content("text/plain", emailBody);
        Mail mail = new Mail(from, subject, to, content);

        // Чтение файла и конвертация в Base64
        // Read file and encode to Base64
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));
        String encodedFile = Base64.getEncoder().encodeToString(fileData);

        // Создание вложения
        // Create attachment
        Attachments attachment = new Attachments();
        attachment.setContent(encodedFile);
        attachment.setType("image/png");
        attachment.setFilename("unauthorized_access.png");
        attachment.setDisposition("attachment");

        // Добавляем вложение в письмо
        // Add attachment to the email
        mail.addAttachments(attachment);
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        int attempt = 0;
        boolean success = false;
        while (attempt < MAX_RETRIES && !success) {
            try {
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());
                Response response = sg.api(request);

                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    System.out.println("Email sent successfully. Status code: " + response.getStatusCode());
                   // System.out.println("Response Body: " + response.getBody());
                   // System.out.println("Response Headers: " + response.getHeaders());
                    success = true;
                } else {
                    System.out.println("Failed to send email. Status code: " + response.getStatusCode());
                    System.out.println("Response Body: " + response.getBody());
                    attempt++;
                    if (attempt < MAX_RETRIES) {
                        System.out.println("Retrying in " + RETRY_DELAY_M / 1000 + " seconds...");
                        try {
                            Thread.sleep(RETRY_DELAY_M * 1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrupted", ie);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException occurred: " + e.getMessage());
                attempt++;
                if (attempt < MAX_RETRIES) {
                    System.out.println("Retrying in " + RETRY_DELAY_M / 1000 + " seconds...");
                    try {
                        Thread.sleep(RETRY_DELAY_M);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                } else {
                    throw e; // Retries exhausted, rethrow exception
                }
            }
        }
    }
}
