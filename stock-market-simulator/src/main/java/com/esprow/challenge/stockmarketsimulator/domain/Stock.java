package com.esprow.challenge.stockmarketsimulator.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @Column(name = "id")
    String symbol;
}
