import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.*;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.*;
import java.util.Scanner;

public class InputManager {
    static EmotionInput emotionInput = new EmotionInput(); 
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        startWebcamThread();
        startAudioThread();

        while (true) {
            System.out.print("Enter Emotion Label (happy/sad/angry/neutral): ");
            String label = scanner.nextLine();
            saveInputToCSV(label);
            System.out.println("✅ Saved sample to dataset.csv\n");

            sleep(5000);  // Delay to wait before asking for another input
        }
    }

    // Start Webcam Thread
    static void startWebcamThread() {
        new Thread(() -> {
            try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0)) {
                grabber.start();
                System.out.println("✅ Webcam started!");

                CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
                CascadeClassifier smileDetector = new CascadeClassifier("haarcascade_smile.xml");

                if (faceDetector.empty() || smileDetector.empty()) {
                    System.out.println("❌ Failed to load cascade classifiers.");
                    return;
                } else {
                    System.out.println("✅ Cascade classifiers loaded successfully!");
                }

                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                CanvasFrame canvasFrame = new CanvasFrame("Webcam Feed", CanvasFrame.getDefaultGamma() / grabber.getGamma());

                while (true) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        System.out.println("❌ No frame captured from webcam!");
                        continue;
                    }

                    Mat image = converter.convert(frame);
                    if (image == null) {
                        System.out.println("❌ Failed to convert frame to Mat!");
                        continue;
                    }

                    RectVector faces = new RectVector();
                    faceDetector.detectMultiScale(image, faces);

                    emotionInput.faceDetected = faces.size() > 0;
                    emotionInput.smileDetected = false;

                    for (int i = 0; i < faces.size(); i++) {
                        Mat faceROI = new Mat(image, faces.get(i));
                        RectVector smiles = new RectVector();
                        smileDetector.detectMultiScale(faceROI, smiles);
                        if (smiles.size() > 0) {
                            emotionInput.smileDetected = true;
                            break;
                        }
                    }

                    // Show the webcam feed with face and smile detection
                    canvasFrame.showImage(frame);

                    if (!canvasFrame.isVisible()) {
                        break;  // If the window is closed, stop the loop
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Webcam Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // Start Audio Thread for Pitch Detection
    static void startAudioThread() {
        new Thread(() -> {
            try {
                AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
                dispatcher.addAudioProcessor(new PitchProcessor(
                        PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                        22050, 1024,
                        (result, e) -> {
                            float pitch = result.getPitch();
                            if (pitch != -1) emotionInput.pitch = pitch;
                        }
                ));
                dispatcher.run();
            } catch (Exception e) {
                System.out.println("❌ Audio Error: " + e.getMessage());
            }
        }).start();
    }

    // Save the input data (pitch, face detected, smile detected, emotion label) to a CSV file
    static void saveInputToCSV(String label) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("dataset.csv", true)))) {
            int face = emotionInput.faceDetected ? 1 : 0;
            int smile = emotionInput.smileDetected ? 1 : 0;
            out.println(emotionInput.pitch + "," + face + "," + smile + "," + label);
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
    }

    // Sleep method to pause the thread for a specified time (in milliseconds)
    static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}
