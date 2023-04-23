package com.example.micrometerexample;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Random;

@RestController
public class ExampleController {
    private final DistributionSummary summary;
    private final Timer timer;
    public ExampleController(MeterRegistry registry) {
         summary = DistributionSummary
                 .builder("controller.summary")
                 .baseUnit("ms")
                 .maximumExpectedValue(50d)
                 .minimumExpectedValue(10d)
                 .publishPercentileHistogram()
                 .publishPercentiles(0.5, 0.99)
                 .serviceLevelObjectives(20)
                 .register(registry);
         timer = Timer
                 .builder("controller.timer")
                 .maximumExpectedValue(Duration.ofMillis(5))
                 .publishPercentileHistogram()
                 .publishPercentiles(0.5, 0.99)
                 .serviceLevelObjectives(Duration.ofMillis(2))
                 .register(registry);
    }
    @GetMapping("/latency")
    public String endpoint() throws Exception {
        return "latency:" + timer.recordCallable(() -> {
            int latency = 10 + new Random().nextInt(50);
            summary.record(latency);
            return latency;
        });
    }

}
