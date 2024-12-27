package net.casim.bc.web;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import net.casim.bc.service.ClassificationService;
import net.casim.bc.service.DetectionService;
import net.casim.bc.service.ForecastingService;
import net.casim.bc.service.SentimentService;
import net.casim.bc.data.ForecastRequest;
import net.casim.bc.data.InferenceResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InferenceController {

    private static final Logger logger = LoggerFactory.getLogger(InferenceController.class);

    private final ClassificationService classificationService;
    private final DetectionService detectionService;
    private final SentimentService sentimentService;
    private final ForecastingService forecastingService;

    public InferenceController(ClassificationService classificationService,
                               DetectionService detectionService,
                               SentimentService sentimentService, ForecastingService forecastingService) {
        this.classificationService = classificationService;
        this.detectionService = detectionService;
        this.sentimentService = sentimentService;

        this.forecastingService = forecastingService;
    }

    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> classifyImage(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received a classification request: {}", file.getOriginalFilename());
            Image img = ImageFactory.getInstance().fromInputStream(file.getInputStream());

            InferenceResult result = classificationService.classifyAndReturnBest(img);

            logger.info("Returning best class: {} with probability {}",
                    result.getClassName(), result.getProbability());
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            logger.error("Error reading the uploaded file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading the uploaded file: " + e.getMessage());
        } catch (TranslateException e) {
            logger.error("Error during image classification: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during image classification: " + e.getMessage());
        }
    }



    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> detectObjects(@RequestParam("image") MultipartFile file) {
        logger.info("Received request for object detection: name={}, size={} bytes", file.getOriginalFilename(), file.getSize());
        try (InputStream is = file.getInputStream()) {
            Image img = ImageFactory.getInstance().fromInputStream(is);
            DetectedObjects detections = detectionService.detect(img);
            logger.info("Object detection completed. Found {} objects.", detections.items().size());
            List<DetectedObjectInfo> objects = detections.items().stream()
                    .map(d -> new DetectedObjectInfo(d.getClassName(), d.getProbability(), d.toString()))
                    .toList();
            return ResponseEntity.ok(new ObjectDetectionResult(objects));
        } catch (Exception e) {
            logger.error("Error processing object detection: ", e);
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }

    @GetMapping("/sentiment")
    public ResponseEntity<?> analyzeSentiment(@RequestParam("text") String text) {
        logger.info("Received request for sentiment analysis: text='{}'", text);
        try {
            Classifications result = sentimentService.analyze(text);
            Classifications.Classification best = result.best();
            logger.info("Sentiment analysis completed. Result: {} with probability {}", best.getClassName(), best.getProbability());
            return ResponseEntity.ok(new SentimentResult(best.getClassName(), best.getProbability()));
        } catch (Exception e) {
            logger.error("Error processing sentiment analysis: ", e);
            return ResponseEntity.status(500).body("Error processing text: " + e.getMessage());
        }
    }

    @PostMapping("/forecast")
    public Map<String, float[]> forecast(@RequestBody ForecastRequest payload) {
        float[] historicalData = payload.getHistoricalData();
        int predictionLength = payload.getPredictionLength();
        float[] forecast;
        try {
            forecast = forecastingService.forecast(historicalData, predictionLength);
        } catch (Exception e) {
            throw new RuntimeException("Forecasting failed: " + e.getMessage(), e);
        }
        return Map.of("forecast", forecast);
    }


    record DetectedObjectInfo(String className, double probability, String boundingBox) {}
    record ObjectDetectionResult(List<DetectedObjectInfo> objects) {}
    record SentimentResult(String sentiment, double probability) {}

}
