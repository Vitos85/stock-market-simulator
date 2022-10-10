package com.esprow.challenge.stockmarketsimulator.rest;

import com.esprow.challenge.stockmarketsimulator.domain.OrderDir;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Validated
public class LimitOrderRequest {

    @NotNull
    private OrderDir dir;

    @Positive
    private int price;

    @Positive
    private int quantity;

    @NotNull
    private String symbol;

    @NotNull
    private String userID;
}
