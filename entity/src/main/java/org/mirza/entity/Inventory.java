package org.mirza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "inventory")
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @UniqueElements
    private String productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean available;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt = new Date();

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
