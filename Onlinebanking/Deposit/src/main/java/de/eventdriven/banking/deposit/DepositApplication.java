package de.eventdriven.banking.deposit;

import de.eventdriven.banking.utils.EventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@SpringBootApplication
public class DepositApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DepositApplication.class, args);
    }

    @Service
    static class DepositManager {

        private final StreamBridge streamBridge;

        @Autowired
        public DepositManager(StreamBridge streamBridge) {
            this.streamBridge = streamBridge;
        }

        @Bean
        public Consumer<EventRequest> deposit() {
            return eventRequest -> {
                LOGGER.info("event received");
                streamBridge.send("repositoryInDeposit-in-0", eventRequest);
            };
        }

        @Bean
        public Consumer<String> repositoryOutDeposit() {
            return s -> {
                LOGGER.info(s);
                streamBridge.send("eventInitiator-in-0", s);
            };
        }
    }
}
