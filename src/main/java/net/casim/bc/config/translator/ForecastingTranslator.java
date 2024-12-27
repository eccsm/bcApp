package net.casim.bc.config.translator;

import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

public class ForecastingTranslator implements Translator<NDList, NDList> {

    @Override
    public NDList processInput(TranslatorContext ctx, NDList input) {
        // If no preprocessing is needed, return the input as is
        return input;
    }

    @Override
    public NDList processOutput(TranslatorContext ctx, NDList list) {
        // If no postprocessing is needed, return the output as is
        return list;
    }

    @Override
    public Batchifier getBatchifier() {
        // Define how batches are handled; STACK is common
        return Batchifier.STACK;
    }
}
