package com.esprow.challenge.stockmarketsimulator.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trades")
public class Trade {

    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "buy_order_id", nullable = false)
    private LimitOrder buyOrder;

    @ManyToOne
    @JoinColumn(name = "sell_order_id", nullable = false)
    private LimitOrder sellOrder;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Setter(value = AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "created", nullable = false)
    private Instant created;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trade other = (Trade) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trade [id=");
        builder.append(id);
        builder.append(", buyOrder=");
        builder.append(buyOrder.getId());
        builder.append(", sellOrder=");
        builder.append(sellOrder.getId());
        builder.append(", price=");
        builder.append(price);
        builder.append(", quantity=");
        builder.append(quantity);
        builder.append(", created=");
        builder.append(created);
        builder.append("]");
        return builder.toString();
    }

}
