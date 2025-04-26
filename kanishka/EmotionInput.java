public class EmotionInput {
    public float pitch = -1;
    public boolean faceDetected = false;
    public boolean smileDetected = false;

    @Override
    public String toString() {
        return "EmotionInput{" +
                "pitch=" + pitch +
                ", faceDetected=" + faceDetected +
                ", smileDetected=" + smileDetected +
                '}';
    }
}
