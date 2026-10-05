package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Product;

/**
 * Repository interface for {@link Product} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations. In addition, this repository declares a set of
 * derived queries and JPQL queries that support product lookups by
 * category, price range, SKU, name pattern and store-specific filters.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves all products in the catalogue.
     *
     * @return the list of all products
     */
    List<Product> findAll();

    /**
     * Retrieves all products belonging to the given category.
     *
     * @param category the category to filter by
     * @return the list of products in the given category
     */
    List<Product> findByCategory(String category);

    /**
     * Retrieves all products whose price falls within the inclusive
     * range defined by the given boundaries.
     *
     * @param minPrice the lower boundary of the price range
     * @param maxPrice the upper boundary of the price range
     * @return the list of products within the price range
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * Retrieves all products matching the given stock keeping unit.
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
     * Retrieves a product by its identifier.
     *
     * <p>The method is named {@code findByid} (with a lowercase "i") to
     * avoid a name clash with the {@code Optional<Product> findById(Long)}
     * method inherited from {@code JpaRepository}.</p>
     *
     * @param id the identifier of the product
     * @return the matching product, or {@code null} if none exists
     */
    Product findByid(Long id);

    /**
     * Retrieves all products available in a given store whose name
     * matches the provided substring, ignoring case differences.
     *
     * @param storeId the identifier of the store
     * @param pname   the substring to search for within the product name
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findByNameLike(long storeId, String pname);

    /**
     * Retrieves all products available in a given store whose name
     * matches the provided substring and whose category matches the
     * given one, ignoring case differences on the name.
     *
     * @param storeId  the identifier of the store
     * @param pname    the substring to search for within the product name
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.product.category = :category")
    List<Product> findByNameAndCategory(long storeId, String pname, String category);

    /**
     * Retrieves all products of a given category available in a specific
     * store.
     *
     * @param storeId  the identifier of the store
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findByCategoryAndStoreId(long storeId, String category);

    /**
     * Retrieves all products whose name contains the given substring,
     * ignoring case differences.
     *
     * @param pname the substring to search for within the product name
     * @return the list of matching products
     */
    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findProductBySubName(String pname);

    /**
     * Retrieves all products available in a specific store regardless of
     * category.
     *
     * @param storeId the identifier of the store
     * @return the list of products available in the store
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    List<Product> findProductsByStoreId(Long storeId);

    /**
     * Retrieves all products of a given category available in a specific
     * store.
     *
     * @param category the category to filter by
     * @param storeId  the identifier of the store
     * @return the list of matching products
     */
    @Query("SELECT i.product FROM Inventory i WHERE i.product.category = :category and i.store.id = :storeId")
    List<Product> findProductByCategory(String category, long storeId);

    /**
     * Retrieves all products whose name contains the given substring and
     * whose category matches the given one, ignoring case differences on
     * the name.
     *
     * @param pname    the substring to search for within the product name
     * @param category the category to filter by
     * @return the list of matching products
     */
    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category")
    List<Product> findProductBySubNameAndCategory(String pname, String category);
}