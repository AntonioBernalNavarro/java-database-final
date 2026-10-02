package com.project.code.Repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderItem;

/**
 * Repository interface for {@link OrderItem} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations (save, delete, findById, findAll, etc.) without requiring any
 * manual implementation. This repository does not need custom query methods
 * because the order item data is always accessed through its parent order.</p>
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // No custom methods required. Standard CRUD is inherited from JpaRepository.
}


