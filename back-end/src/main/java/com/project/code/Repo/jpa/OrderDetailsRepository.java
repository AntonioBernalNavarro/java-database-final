package com.project.code.Repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderDetails;

/**
 * Repository interface for {@link OrderDetails} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations (save, delete, findById, findAll, etc.) without requiring any
 * manual implementation. This repository does not need custom query methods
 * because order details are always retrieved through the standard persistence
 * operations or by joining with the associated customer or store.</p>
 */
@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    // No custom methods required. Standard CRUD is inherited from JpaRepository.
}

