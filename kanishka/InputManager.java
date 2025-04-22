import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.*;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

public class InputManager {
    public static EmotionInput emotionInput = new EmotionInput();

    public static void main(String[] args) throws Exception {
        // Start webcam thread
        new Thread(() -> startWebcam()).start();

        // Start mic processing
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        PitchDetectionHandler handler = (result, e) -> {
            float pitch = result.getPitch();
            if (pitch != -1) {
                emotionInput.pitch = pitch;
                System.out.println("🎙 Pitch: " + pitch + " Hz");
            }
        };
        dispatcher.addAudioProcessor(new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                22050, 1024, handler));
        new Thread(dispatcher).start();

        // Watch the input object in real time
        while (true) {
            Thread.sleep(1000);
            System.out.println("🧠 Current Input: " + emotionInput);
        }
    }

    static void startWebcam() {
        try {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();

            CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
            CanvasFrame canvas = new CanvasFrame("Webcam Feed");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            while (canvas.isVisible()) {
                Frame frame = grabber.grab();
                Mat image = converter.convert(frame);
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(image, faces);
                emotionInput.faceDetected = (faces.size() > 0);
                for (int i = 0; i < faces.size(); i++) {
                    Rect face = faces.get(i);
                    rectangle(image, face, new Scalar(0, 255, 0, 1));
                }
                canvas.showImage(converter.convert(image));
            }

            grabber.stop();
            canvas.dispose();
        } catch (Exception e) {
            System.out.println("❌ Webcam Error: " + e.getMessage());
        }
    }
}
