package com.project.code.Repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Customer;

/**
 * Repository interface for {@link Customer} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations (save, delete, findById, findAll, etc.) without requiring any
 * manual implementation. In addition to the inherited operations, this
 * repository declares custom query methods to support lookups by email
 * and by other business-relevant fields.</p>
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Retrieves a customer by their email address.
     *
     * @param email the email address to search for
     * @return the matching customer, or {@code null} if none exists
     */
    Customer findByEmail(String email);
}
