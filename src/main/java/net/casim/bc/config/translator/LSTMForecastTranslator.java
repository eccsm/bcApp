package net.casim.bc.config.translator;

import ai.djl.ndarray.*;
import ai.djl.translate.*;

/**
 * A simple translator that receives a NDList input (like a single NDArray shaped [1, window, features])
 * and returns NDList containing the forecast.
 */
public class LSTMForecastTranslator implements Translator<NDList, NDList> {

    @Override
    public NDList processInput(TranslatorContext ctx, NDList input) {
        // We expect input already in shape [1, window, features] or something similar.
        // If you need to reshape or manipulate, do it here.
        return input;
    }

    @Override
    public NDList processOutput(TranslatorContext ctx, NDList list) {
        // The model's output is presumably [1, 1] or [1, steps], depending on your model.
        // We can directly return the NDList or parse it.
        return list;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;  // we handle single input at a time
    }
}

