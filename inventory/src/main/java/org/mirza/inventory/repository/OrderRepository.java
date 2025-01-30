package org.mirza.inventory.repository;

import org.mirza.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths={"orderDetail", "orderDetail.inventory"})
    Optional<Order> findOrderById(long id);
}
