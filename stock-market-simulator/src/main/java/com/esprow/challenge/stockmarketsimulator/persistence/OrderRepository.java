package com.esprow.challenge.stockmarketsimulator.persistence;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<LimitOrder, UUID> {

}
