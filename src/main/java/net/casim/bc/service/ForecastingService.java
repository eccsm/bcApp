package net.casim.bc.service;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import net.casim.bc.properties.DjlCommonProperties;
import net.casim.bc.properties.ForecastingProperties;
import net.casim.bc.config.translator.ForecastingTranslator;
import net.casim.bc.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class ForecastingService {

    private static final Logger logger = LoggerFactory.getLogger(ForecastingService.class);

    private final ZooModel<NDList, NDList> model;
    private final ForecastingProperties properties;

    public ForecastingService(ForecastingProperties properties, DjlCommonProperties djlProps) throws IOException, ModelException {
        this.properties = properties;

        Path tempModelPath = ModelUtils.copyModelFromClasspath(
                properties.getModelResourcePath(),
                getClass()
        );

        Criteria<NDList, NDList> criteria = Criteria.builder()
                .setTypes(NDList.class, NDList.class)
                .optModelPath(tempModelPath)
                .optEngine(djlProps.getEngine())
                .optTranslator(new ForecastingTranslator())
                .optOption("mapLocation", properties.getMapLocation())
                .build();

        this.model = criteria.loadModel();
        logger.info("Forecasting model loaded successfully from: {} (temp path: {})",
                properties.getModelResourcePath(), tempModelPath);
    }

    public float[] forecast(float[] historicalData, int predictionLength) throws TranslateException {

        int maxPredictionLength = properties.getMaxPredictionLength();
        if (predictionLength <= 0 || predictionLength > maxPredictionLength) {
            throw new IllegalArgumentException("prediction_length must be between 1 and " + maxPredictionLength);
        }

        int window = properties.getWindow();
        int features = properties.getFeatures();
        if (historicalData.length != window * features) {
            throw new IllegalArgumentException("Expected " + (window * features)
                    + " data points, got " + historicalData.length);
        }

        NDManager manager = model.getNDManager();
        NDArray inputNd = manager.create(historicalData, new Shape(window, features));
        inputNd = inputNd.expandDims(0);

        NDList inputList = new NDList(inputNd);

        try (Predictor<NDList, NDList> predictor = model.newPredictor()) {
            NDList outputList = predictor.predict(inputList);
            NDArray output = outputList.get(0);
            float[] preds = output.toFloatArray();
            return Arrays.copyOfRange(preds, 0, predictionLength);
        }
    }
}
