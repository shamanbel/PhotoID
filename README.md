# Photo Identifier

## Description
This application checks the user's photo at Windows startup and compares it with the reference photo taken during configuration. In case of a mismatch and if an internet connection is available, the program sends the current user's photo and their IP address to the email specified during configuration. If there is no internet connection, the program will enter a waiting mode, where it will attempt to send the message at regular intervals. The program is designed for use on laptops (Windows).

## Requirements
- Windows 10/11
- Java 17 (JRE)
<!-- IntelliJ IDEA (for development)
- Maven (for building the project)
- Internet connection (for sending emails)-->

## Installation
1. Download the folder:    Photo_identifier_default
    from the repository:  https://github.com/shamanbel/PhotoID.git
   
2. Place it in a convenient location.
3. Run the file "start_Photo_identifier.bat".


Upon the first run, the program will create the config.properties file and ask you to provide the following information:
- email.address – the owner's email for sending notifications.
- similarity threshold – determines the probability threshold for matching the owner's and user's photos.
- owner's photo – the camera will take a photo, which will later be used for comparison with user photos.

The program will then exit. Upon subsequent runs, the established configuration will be used. To change the configuration, delete the "src" folder and repeat the setup process.
    
4. Set up the auto-start of "start_Photo_identifier.bat" in the Windows Task Scheduler with the following settings:
-  In the "General" tab, check the box "Run with the highest privileges."
-   In the "Triggers" tab, click "New" and select "At startup." Enable the option "Delay task" and set it to 10 seconds.
-   In the "Actions" tab, click "New" and select "Start a program." Specify the path to your batch file and the application's working folder.

## Image Processing Description 
The image processing algorithm in the code performs two key tasks:

-   compares images by their histograms;
-   detects faces using cascade classifiers.

Both images are converted to grayscale using the Imgproc.cvtColor function.
This
