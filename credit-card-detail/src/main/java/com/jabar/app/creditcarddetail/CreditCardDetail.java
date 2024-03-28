package com.jabar.app.creditcarddetail;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CreditCardDetail(
        String placeholder,
        String cardNumber) implements Serializable {
}