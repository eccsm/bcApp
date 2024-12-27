package net.casim.bc.service;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.*;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import net.casim.bc.properties.DjlCommonProperties;
import net.casim.bc.properties.SentimentProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SentimentService {

    private final DjlCommonProperties djlProps;
    private final SentimentProperties sentimentProps;

    public SentimentService(DjlCommonProperties djlProps, SentimentProperties sentimentProps) {
        this.djlProps = djlProps;
        this.sentimentProps = sentimentProps;
    }

    public Classifications analyze(String text) throws IOException, ModelException, TranslateException {

        Criteria<String, Classifications> criteria = Criteria.builder()
                .setTypes(String.class, Classifications.class)
                .optModelUrls(sentimentProps.getModelUrl())
                .optEngine(djlProps.getEngine())
                .optDevice(Device.fromName(djlProps.getDevice()))
                .optProgress(new ProgressBar())
                .build();

        try (ZooModel<String, Classifications> model = ModelZoo.loadModel(criteria);
             Predictor<String, Classifications> predictor = model.newPredictor()) {
            return predictor.predict(text);
        }
    }
}
