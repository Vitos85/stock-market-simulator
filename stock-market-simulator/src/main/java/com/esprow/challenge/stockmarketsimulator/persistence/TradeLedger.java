package com.esprow.challenge.stockmarketsimulator.persistence;

import com.esprow.challenge.stockmarketsimulator.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TradeLedger extends JpaRepository<Trade, UUID> {

}
