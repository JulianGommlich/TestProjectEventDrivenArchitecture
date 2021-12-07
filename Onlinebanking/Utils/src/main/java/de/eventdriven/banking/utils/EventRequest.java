package de.eventdriven.banking.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequest {

    @NotNull
    @Size(min = 10, max = 10)
    @JsonProperty("account_number")
    String accountNumber;

    @NotNull
    @Size(min = 4, max = 4)
    @JsonProperty("pin_code")
    String pinCode;

    @NotNull
    @JsonProperty("event_type")
    EventType eventType;

    @Size(max = 30)
    @Pattern(regexp = "[A-Za-z\\s]+")
    String accountant;

    Double amount;
}
