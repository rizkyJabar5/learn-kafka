package com.jabar.app.creditcarddetail;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.jabar.app.creditcarddetail.CreditCardDetailConsumerConfig.GROUP_PAYMENT_ID;

@Component
@Slf4j
public class CreditCardNumberListener {
    public static final String CARD_DETAIL = "card-detail";

    @KafkaListener(
            topics = CARD_DETAIL,
            groupId = GROUP_PAYMENT_ID,
            containerFactory = "creditCardDetailListenerContainerFactory"
    )
    public void listener(@Payload CreditCardDetail data) {
        System.out.println("Listener Received: " + data);
    }
}
