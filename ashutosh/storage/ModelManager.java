package storage;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

public class ModelManager {

    public static void saveModel(Classifier model, String path) throws Exception {
        SerializationHelper.write(path, model);
    }

    public static Classifier loadModel(String path) throws Exception {
        return (Classifier) SerializationHelper.read(path);
    }
}