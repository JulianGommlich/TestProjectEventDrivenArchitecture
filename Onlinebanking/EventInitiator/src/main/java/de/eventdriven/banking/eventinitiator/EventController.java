package de.eventdriven.banking.eventinitiator;

import de.eventdriven.banking.utils.Account;
import de.eventdriven.banking.utils.EventRequest;
import de.eventdriven.banking.utils.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class EventController {

    private StreamBridge streamBridge;

    @Autowired
    public EventController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> createBankAccount(@RequestBody Account account) {
        EventRequest eventRequest = new EventRequest(
                account.getAccountNumber(),
                account.getPinCode(),
                EventType.CREATE_ACCOUNT,
                account.getAccountant(),
                null);

        streamBridge.send("createAccount-in-0", eventRequest);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deposits")
    public ResponseEntity<Object> depositMoney(@RequestParam Double depositAmount, @RequestBody Account account) {
        EventRequest eventRequest = new EventRequest(
                account.getAccountNumber(),
                account.getPinCode(),
                EventType.DEPOSIT,
                account.getAccountant(),
                depositAmount);

        streamBridge.send("deposit-in-0", eventRequest);

        return ResponseEntity.noContent().build();
    }
}
