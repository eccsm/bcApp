package net.casim.bc.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModelUtils {

    private ModelUtils() {
        // Utility class; prevent instantiation
    }

    public static Path copyModelFromClasspath(String resourcePath, Class<?> contextClass) throws IOException {
        try (InputStream is = contextClass.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Model resource not found: " + resourcePath);
            }
            Path tempFile = Files.createTempFile("model-", ".pt");
            tempFile.toFile().deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        }
    }
}
