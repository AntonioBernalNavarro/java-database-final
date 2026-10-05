package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderItem;

/**
 * Repository interface for {@link OrderItem} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations (save, delete, findById, findAll, etc.) without
 * requiring any manual implementation.</p>
 *
 * <p>No custom query methods are declared because the order items are
 * always accessed through the standard persistence operations or by
 * navigating the relationships declared in the entity itself.</p>
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}


