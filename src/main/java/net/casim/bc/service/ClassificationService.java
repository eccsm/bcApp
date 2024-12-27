package net.casim.bc.service;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.*;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import jakarta.annotation.PreDestroy;
import net.casim.bc.properties.ClassificationProperties;
import net.casim.bc.data.InferenceResult;
import net.casim.bc.utils.ModelUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassificationService {

    private final Predictor<Image, Classifications> predictor;
    private final Model model;

    public ClassificationService(ClassificationProperties props) throws IOException, ModelException, TranslateException {
        Path tempModelFile = ModelUtils.copyModelFromClasspath(props.getModelPath(), getClass());

        model = Model.newInstance("my_resnet18", "PyTorch");
        model.load(tempModelFile, "");

        InputStream synsetStream = getClass().getResourceAsStream(props.getSynsetPath());
        if (synsetStream == null) {
            throw new IOException("Synset file not found at " + props.getSynsetPath());
        }
        List<String> classNames;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(synsetStream))) {
            classNames = reader.lines().collect(Collectors.toList());
        }

        Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                .addTransform(new Resize(256))
                .addTransform(new CenterCrop())
                .addTransform(new ToTensor())
                .addTransform(new Normalize(
                        new float[]{0.485f, 0.456f, 0.406f},
                        new float[]{0.229f, 0.224f, 0.225f}))
                .optApplySoftmax(true)
                .optSynset(classNames)
                .build();

        predictor = model.newPredictor(translator);
    }

    public InferenceResult classifyAndReturnBest(Image img) throws TranslateException {
        Classifications classifications = predictor.predict(img);
        Classifications.Classification best = classifications.best();
        return new InferenceResult(best.getClassName(), best.getProbability());
    }

    @PreDestroy
    public void close() {
        predictor.close();
        model.close();
    }
}
