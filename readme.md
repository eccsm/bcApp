# bcApp

A Spring Boot application demonstrating various AI/ML model inference services using the Deep Java Library (DJL). This project features:

- **Image Classification** with a custom ResNet model
- **Object Detection** with an SSD model
- **Sentiment Analysis** with DistilBERT
- **Forecasting** with a custom LSTM multi-variate time-series model

## Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Configuration](#configuration)
- [Endpoints](#endpoints)
- [License](#license)

---

## Features

- **ClassificationService**: Classifies uploaded images using a PyTorch-based ResNet model.
- **DetectionService**: Detects objects in images.
- **SentimentService**: Performs sentiment analysis (positive/negative) on text using DistilBERT.
- **ForecastingService**: Forecasts time series data using an LSTM-based PyTorch model.

All models are loaded at runtime from either:
1. Local files (packaged in `src/main/resources`), or
2. DJL’s model zoo.

---

## Project Structure
    bcApp
    └── pom.xml                            
    └── src
        └── main
            └── java
                └──net
                    └── casim
                        ├── bc             
                        ├── config         
                        ├── data           
                        ├── service        
                        └── web            
                └── resources
                ├── application.properties
                └── models               


---

## Getting Started

### Prerequisites

Ensure you have the following installed:
- Java 17+ (or the version you use)
- Maven 3.8+ (or Gradle if preferred)
- A local or remote Git repository to push code

(Optional) Add PyTorch engine dependency for DJL in your `pom.xml`:

```xml
<dependency>
  <groupId>ai.djl.pytorch</groupId>
  <artifactId>pytorch-engine</artifactId>
  <version>0.31.0</version> <!-- Or your chosen DJL version -->
</dependency>
```

## Building and Running

### Clone this Repository

To get started, clone the repository and navigate to the project directory:

```bash
git clone https://github.com/<YOUR_USERNAME>/<YOUR_REPO_NAME>.git
cd <YOUR_REPO_NAME>
```

### Build with Maven

Use Maven to build the project:

```bash
mvn clean install
```

### Run the Spring Boot Application
Run the application with Maven:

```bash
mvn spring-boot:run
```
By default, the application will start on port 8080.

### Usage
Once the application is running, you can interact with the provided endpoints using a REST client or curl. For example, to classify an image:

```bash
curl -X POST http://localhost:8080/api/classify \
-H "Content-Type: multipart/form-data" \
-F "file=@path/to/your/image.jpg"
```
### Configuration
All configuration properties, such as model paths, translator settings, and engine names, are stored in the application.properties file. Below is an example configuration:

```ini
casim.forecasting.model-resource-path=/models/lstm_multivar/forecast_lstm_multivar_ts.pt
casim.forecasting.max-prediction-length=28
casim.forecasting.window=14
casim.forecasting.features=4
casim.forecasting.engine=PyTorch
casim.forecasting.map-location=true

casim.classification.model-path=/models/my_resnet18/my_resnet18.pt
casim.classification.synset-path=/models/my_resnet18/synset.txt

casim.djl.engine=PyTorch
casim.djl.device=cpu
casim.sentiment.model-url=djl://ai.djl.pytorch/distilbert
```

### Switching to GPU
To switch the application to use GPU instead of CPU, update the configuration:

```ini
casim.djl.device=gpu
```

### Endpoints
The application exposes the following REST API endpoints:

Endpoint	Method	Description
/api/classify	POST	Classifies an image. Use form-data parameter file for the image.
/api/detect	POST	Detects objects in an image. Use form-data parameter image.
/api/sentiment	GET	Analyzes sentiment. Use query parameter text=your+text+here.
/api/forecast	POST	Forecasts time series data. Send JSON with historicalData and predictionLength.
### Example Forecast Request
Below is an example JSON payload for a time series forecasting request:

```json
{
"historicalData": [1.2, 3.4, 2.2],
"predictionLength": 7
}
```

License
plaintext
Kodu kopyala
MIT License

Copyright (c) 2024 ...

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
