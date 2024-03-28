package com.jabar.app.creditcardnumber;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.jabar.app.creditcardnumber.CreditCardNumberConsumerConfig.GROUP_PAYMENT_ID;


@Component
@Slf4j
public class CreditCardNumberListener {
    public static final String CARD_NUMBER = "card-number";


    @KafkaListener(
            topics = CARD_NUMBER,
            groupId = GROUP_PAYMENT_ID,
            containerFactory = "creditCardNumberListenerContainerFactory"
    )
    public void listener(CreditCardNumber data) {
        System.out.println("Listener Received: " + data);
    }
}
