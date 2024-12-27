package net.casim.bc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "casim.djl")
@Data
public class DjlCommonProperties {

    private String engine;
    private String device;

}
