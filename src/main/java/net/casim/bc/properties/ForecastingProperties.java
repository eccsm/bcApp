package net.casim.bc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "casim.forecasting")
@Data
public class ForecastingProperties {

    private String modelResourcePath;
    private int maxPredictionLength;
    private int window;
    private int features;
    private String mapLocation;
}
