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
@Table(name = "l_order")
public class LimitOrder {

    @Setter(value = AccessLevel.NONE)
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_dir", nullable = false)
    private OrderDir orderDir;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "rest", nullable = false)
    private int rest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.OPEN;

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
        LimitOrder other = (LimitOrder) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Order [id=");
        builder.append(id);
        builder.append(", symbol=");
        builder.append(symbol);
        builder.append(", dir=");
        builder.append(orderDir);
        builder.append(", price=");
        builder.append(price);
        builder.append(", quantity=");
        builder.append(quantity);
        builder.append(", rest=");
        builder.append(rest);
        builder.append(", status=");
        builder.append(status);
        builder.append(", created=");
        builder.append(created);
        builder.append("]");
        return builder.toString();
    }
}
