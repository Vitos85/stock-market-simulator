package com.esprow.challenge.stockmarketsimulator.persistence;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<LimitOrder, UUID> {

    @Query("SELECT o FROM LimitOrder o " +
            "WHERE o.status IN :status")
    List<LimitOrder> findAllOpenOrders(OrderStatus... status);

}
