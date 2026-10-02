package com.project.code.Repo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Product;

/**
 * Repository interface for {@link Product} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations (save, delete, findById, findAll, etc.) without requiring any
 * manual implementation. In addition, this repository declares a set of
 * custom derived queries and JPQL queries that support product lookups by
 * category, price range, SKU, name pattern and store-specific filters.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves all products belonging to the given category.
     *
     * @param category the category to filter by
     * @return the list of products in the given category
     */
    List<Product> findByCategory(String category);

    /**
     * Retrieves all products whose price falls within the inclusive range
     * defined by the given boundaries.
     *
     * @param minPrice the lower boundary of the price range
     * @param maxPrice the upper boundary of the price range
     * @return the list of products within the price range
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * Retrieves all products matching the given stock keeping unit.
     *
     * <p>The method returns a list rather than a single product because
     * the SKU uniqueness constraint is enforced at the database level but
     * the query does not assume a single result at the Java level.</p>
     *
     * @param sku the SKU to search for
     * @return the list of products matching the SKU
     */
    List<Product> findBySku(String sku);

    /**
     * Retrieves a product by its exact name.
     *
     * @param name the name of the product
     * @return the matching product, or {@code null} if none exists
     */
    Product findByName(String name);

    /**
     * Retrieves all products available in a given store whose name matches
     * the provided substring, ignoring case differences.
     *
     * <p>The query joins the {@code Inventory} entity with {@code Product}
     * to restrict the search to the requested store and applies a
     * case-insensitive partial match on the product name.</p>
     *
     * @param storeId the identifier of the store
     * @param pname   the substring to search for within the product name
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i "
        + "WHERE i.store.id = :storeId "
        + "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findByNameLike(@Param("storeId") Long storeId,
                                 @Param("pname") String pname);

    /**
     * Retrieves all products available in a given store whose name matches
     * the provided substring and whose category matches the given one,
     * ignoring case differences on the name.
     *
     * @param storeId  the identifier of the store
     * @param pname    the substring to search for within the product name
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i "
        + "WHERE i.store.id = :storeId "
        + "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) "
        + "AND i.product.category = :category")
    List<Product> findByNameAndCategory(@Param("storeId") Long storeId,
                                        @Param("pname") String pname,
                                        @Param("category") String category);

    /**
     * Retrieves all products of a given category available in a specific
     * store.
     *
     * @param storeId  the identifier of the store
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i "
        + "WHERE i.store.id = :storeId "
        + "AND i.product.category = :category")
    List<Product> findByCategoryAndStoreId(@Param("storeId") Long storeId,
                                           @Param("category") String category);

    /**
     * Retrieves all products whose name contains the given substring,
     * ignoring case differences.
     *
     * <p>Unlike {@code findByNameLike}, this query does not restrict the
     * results by store and operates directly on the {@code Product}
     * entity.</p>
     *
     * @param pname the substring to search for within the product name
     * @return the list of matching products
     */
    @Query("SELECT p FROM Product p "
        + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findProductBySubName(@Param("pname") String pname);

    /**
     * Retrieves all products available in a specific store regardless of
     * category.
     *
     * @param storeId the identifier of the store
     * @return the list of products available in the store
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    List<Product> findProductsByStoreId(@Param("storeId") Long storeId);

    /**
     * Retrieves all products of a given category available in a specific
     * store.
     *
     * <p>This method is functionally equivalent to
     * {@link #findByCategoryAndStoreId(Long, String)} but exposes the
     * parameters in a different order to match the controller usage.</p>
     *
     * @param category the category to filter by
     * @param storeId  the identifier of the store
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i "
        + "WHERE i.product.category = :category "
        + "AND i.store.id = :storeId")
    List<Product> findProductByCategory(@Param("category") String category,
                                        @Param("storeId") Long storeId);

    /**
     * Retrieves all products whose name contains the given substring and
     * whose category matches the given one, ignoring case differences on
     * the name.
     *
     * @param pname    the substring to search for within the product name
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT p FROM Product p "
        + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :pname, '%')) "
        + "AND p.category = :category")
    List<Product> findProductBySubNameAndCategory(@Param("pname") String pname,
                                                  @Param("category") String category);
}
