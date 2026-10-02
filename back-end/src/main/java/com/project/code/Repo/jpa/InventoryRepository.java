package com.project.code.Repo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.code.Model.Inventory;

/**
 * Repository interface for {@link Inventory} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations. In addition, this repository declares custom queries to
 * retrieve inventory entries by product-store combinations and to delete
 * inventory entries associated with a given product.</p>
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Retrieves the inventory entry that links a specific product with a
     * specific store.
     *
     * <p>The query navigates both {@code @ManyToOne} associations using
     * the {@code product.id} and {@code store.id} paths, allowing the
     * caller to obtain the unique stock record for a given pair.</p>
     *
     * @param productId the identifier of the product
     * @param storeId   the identifier of the store
     * @return the matching inventory entry, or {@code null} if none exists
     */
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Inventory findByProductIdandStoreId(@Param("productId") Long productId,
                                        @Param("storeId") Long storeId);

    /**
     * Retrieves all inventory entries associated with a given store.
     *
     * <p>This method uses Spring Data's property-path derivation syntax:
     * the underscore in {@code Store_Id} explicitly separates the
     * {@code store} association from its {@code id} attribute, avoiding
     * any ambiguity in the parser.</p>
     *
     * @param storeId the identifier of the store
     * @return the list of inventory entries held by the store
     */
    List<Inventory> findByStore_Id(Long storeId);

    /**
     * Deletes all inventory entries associated with the given product.
     *
     * <p>Because this operation modifies the database, it must be annotated
     * with {@code @Modifying} to inform Spring Data JPA that the query is
     * not a SELECT. The {@code @Transactional} annotation ensures the
     * operation executes within a managed transaction, which is mandatory
     * for any modifying query executed through a repository interface.</p>
     *
     * @param productId the identifier of the product whose inventory
     *                  entries must be removed
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Inventory i WHERE i.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
