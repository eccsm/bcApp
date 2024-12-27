package net.casim.bc.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForecastRequest {
    private float[] historicalData;
    private LocalDateTime startTime;
    private int predictionLength;

}

