package com.jabar.app.creditcardnumber;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
@Slf4j
public class CreditCardNumberConsumerConfig {
    public static final String GROUP_PAYMENT_ID = "payment-id";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    ConsumerFactory<String, CreditCardNumber> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_PAYMENT_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.jabar.app.creditcardnumber.CreditCardNumber");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Object.class)
        );
    }

    @Bean
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        /*
         * here retry policy is used to set the number of attempts to retry and what
         * exceptions you wanted to try and what you don't want to retry.
         */
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();

        // the boolean value in the map determines whether exception should be retried
        exceptionMap.put(IllegalArgumentException.class, false);
        exceptionMap.put(TimeoutException.class, true);
        exceptionMap.put(ListenerExecutionFailedException.class, true);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, exceptionMap, true);

        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CreditCardNumber> creditCardNumberListenerContainerFactory(ConsumerFactory<String, CreditCardNumber> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CreditCardNumber> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
