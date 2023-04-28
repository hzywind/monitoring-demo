package com.example.micrometerexample;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

@RestController
public class ExampleController {
    private final Timer timer;
    public ExampleController(MeterRegistry registry) {
         timer = Timer
                 .builder("controller.timer")
                 .minimumExpectedValue(Duration.ofMillis(30))
                 .maximumExpectedValue(Duration.ofMillis(500))
                 .publishPercentileHistogram()
                 .publishPercentiles(0.5, 0.99)
                 .serviceLevelObjectives(Duration.ofMillis(100))
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
            connection.disconnect();
            Date end = new Date();
            return end.getTime() - start.getTime();
        });
    }
}
