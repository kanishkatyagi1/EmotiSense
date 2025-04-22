// Import JavaCV classes
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;


public class FaceCapture {
    public static void main(String[] args) throws Exception {
        // Step 1: Start Webcam
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 0 = default webcam
        grabber.start();

        // Step 2: Load Haar Cascade Face Detector (like an AI model to find faces)
        CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");

        // Step 3: Create a live webcam window
        CanvasFrame canvas = new CanvasFrame("Webcam Feed");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        // Step 4: Start capturing frames
        while (canvas.isVisible()) {
            Frame frame = grabber.grab(); // Grab a frame from webcam

            // Convert frame to image format
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Mat image = converter.convert(frame);

            // Detect faces
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(image, faces);

            // Draw rectangles on detected faces
            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                rectangle(image, face, Scalar.RED); // Draw red box on face
            }

            // Show the frame with faces highlighted
            canvas.showImage(converter.convert(image));
        }

        grabber.stop();
        canvas.dispose();
    }
}
