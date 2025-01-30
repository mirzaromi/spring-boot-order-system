package org.mirza.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mirza.entity.enums.OrderStatusEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderDetail> orderDetail;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(nullable = false)
    private int totalItems;

    @Column(nullable = false)
    private double totalPrice;

    private String remark;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;

    /**
     * On create.
     */
    @PrePersist
    public void onCreate() {
        createdAt = updatedAt = new Date();
    }

    /**
     * On update.
     */
    @PreUpdate
    public void onUpdate() {
        updatedAt = new Date();
    }
}
