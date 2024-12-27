package net.casim.bc.service;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.*;
import ai.djl.translate.TranslateException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DetectionService {

    public DetectedObjects detect(Image img) throws IOException, ModelException, TranslateException {
        Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                .setTypes(Image.class, DetectedObjects.class)
                .optArtifactId("ssd")
                .build();

        try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(criteria);
             Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            return predictor.predict(img);
        }
    }
}
