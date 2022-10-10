package com.esprow.challenge.stockmarketsimulator.persistence;

import com.esprow.challenge.stockmarketsimulator.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {

}
