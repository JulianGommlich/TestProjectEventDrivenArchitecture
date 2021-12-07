package de.eventdriven.banking.createaccount;

import de.eventdriven.banking.utils.Account;
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
public class CreateAccountApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CreateAccountApplication.class, args);
    }

    @Service
    static class CreateAccountManager {

        private final StreamBridge streamBridge;

        @Autowired
        public CreateAccountManager(StreamBridge streamBridge) {
            this.streamBridge = streamBridge;
        }

        @Bean
        public Consumer<EventRequest> createAccount() {
            return eventRequest -> {
                LOGGER.info("event received");
                streamBridge.send("repositoryIn-in-0", new Account(
                        eventRequest.getAccountNumber(),
                        eventRequest.getPinCode(),
                        eventRequest.getAccountant(),
                        1000.00
                ));
            };
        }

        @Bean
        public Consumer<Account> repositoryOut() {
            return account -> streamBridge.send("eventInitiator-in-0", account.getAccountNumber() + " saved");
        }
    }
}
