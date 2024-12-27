package net.casim.bc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "casim.classification")
@Data
public class ClassificationProperties {

    private String modelPath;
    private String synsetPath;

}
