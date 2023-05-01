package com.example.micrometerexample;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

@RestController
public class ExampleController {
    private final Timer timer;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleController.class);

    public ExampleController(MeterRegistry registry) {
         timer = Timer
                 .builder("controller.timer")
                 .minimumExpectedValue(Duration.ofMillis(30))
                 .maximumExpectedValue(Duration.ofMillis(2000))
                 .publishPercentileHistogram()
                 .publishPercentiles(0.5, 0.99)
                 .serviceLevelObjectives(Duration.ofMillis(500))
                 .register(registry);
    }
    @GetMapping("/latency")
    public String latency() throws Exception {
        return "latency:" + timer.recordCallable(() -> {
            Date start = new Date();
            URL url = new URL("http://www.bing.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            byte[] buf = new byte[2048];
            try {
                InputStream is = connection.getInputStream();
                int ret;
                while ((ret = is.read(buf)) > 0) {
                    processBuf(buf, ret);
                }
                // close the input stream
                is.close();
            } catch (IOException e) {
                InputStream es = connection.getErrorStream();
                int ret;
                // read the response body
                while ((ret = es.read(buf)) > 0) {
                    processBuf(buf, ret);
                }
                // close the error stream
                es.close();
            }
            connection.disconnect();
            Date end = new Date();
            return end.getTime() - start.getTime();
        });
    }

    private void processBuf(byte[] buf, int ret) {
        LOGGER.debug("{} bytes processed", ret);
    }
}
