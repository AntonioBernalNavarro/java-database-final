package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderItem;

import jakarta.transaction.Transactional;

/**
 * Repository interface for {@link OrderItem} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations. In addition to the inherited operations, this
 * repository declares a custom modifying query that deletes all order
 * items associated with a given product. This method is required by
 * {@code ProductController.deleteProduct} to release the foreign key
 * references before deleting the product itself.</p>
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Deletes all order items associated with the given product.
     *
     * <p>Because this operation modifies the database, it must be
     * annotated with {@code @Modifying} to inform Spring Data JPA that
     * the query is not a SELECT. The {@code @Transactional} annotation
     * ensures the operation executes within a managed transaction, which
     * is mandatory for any modifying query executed through a repository
     * interface.</p>
     *
     * @param productId the identifier of the product whose order items
     *                  must be removed
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem o WHERE o.product.id = :productId")
    void deleteByProductId(Long productId);
}


