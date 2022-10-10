package com.esprow.challenge.stockmarketsimulator.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.esprow.challenge.stockmarketsimulator.StockMarketSimulatorApplication;
import com.esprow.challenge.stockmarketsimulator.domain.OrderDir;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = StockMarketSimulatorApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class TradingGatewayControllerTest {
    private static final String USER_ID = "Warren Buffett";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    final void addNewOrder_thenStatus200() throws Exception {
        LimitOrderRequest request = new LimitOrderRequest();
        request.setUserID(USER_ID);
        request.setDir(OrderDir.BUY);
        request.setPrice(30_000);
        request.setQuantity(100);
        request.setSymbol("AAPL");
        mvc.perform(post("/api/v1/orders/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    final void addIncorrectOrder_thenStatus400() throws Exception {
        LimitOrderRequest request = new LimitOrderRequest();
        request.setUserID(USER_ID);
        request.setDir(OrderDir.BUY);
        request.setPrice(0);
        request.setQuantity(0);
        request.setSymbol("");
        mvc.perform(post("/api/v1/orders/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
