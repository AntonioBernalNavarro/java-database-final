package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderDetails;

/**
 * Repository interface for {@link OrderDetails} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations (save, delete, findById, findAll, etc.) without
 * requiring any manual implementation.</p>
 *
 * <p>No custom query methods are declared because the order details are
 * always accessed through the standard persistence operations or by
 * joining with the associated customer or store when required.</p>
 */
@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}

