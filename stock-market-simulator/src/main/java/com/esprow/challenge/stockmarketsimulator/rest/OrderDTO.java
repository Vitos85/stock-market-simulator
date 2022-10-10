package com.esprow.challenge.stockmarketsimulator.rest;

import com.esprow.challenge.stockmarketsimulator.domain.OrderDir;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrderDTO {
    private UUID orderID;
    private String userID;
    private OrderDir dir;
    private int quantity;
    private String stockSymbol;
    private int price;
    private Instant created;
    private Instant cancelled;
    private OrderStatus status;
}
