package de.eventdriven.banking.eventinitiator;

import de.eventdriven.banking.utils.Account;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@SpringBootApplication
public class EventInitiatorApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventInitiatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventInitiatorApplication.class, args);
    }

    @Service
    @NoArgsConstructor
    static class EventManager {

        @Bean
        public Consumer<String> eventInitiator() {
            return LOGGER::info;
        }

    }

}
