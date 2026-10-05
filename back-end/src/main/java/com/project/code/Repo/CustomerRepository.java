package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Customer;

/**
 * Repository interface for {@link Customer} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations (save, delete, findById, findAll, etc.) without
 * requiring any manual implementation. In addition to the inherited
 * operations, this repository declares custom query methods that
 * support lookups by email and by identifier.</p>
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

    /**
     * Retrieves a customer by their identifier.
     *
     * <p>The method is named {@code findByid} (with a lowercase "i") to
     * avoid a name clash with the {@code Optional<Customer> findById(Long)}
     * method inherited from {@code JpaRepository}. Spring Data resolves
     * the derived query against the {@code id} attribute of the entity,
     * so both spellings produce equivalent queries.</p>
     *
     * @param id the identifier of the customer
     * @return the matching customer, or {@code null} if none exists
     */
    Customer findByid(Long id);
}