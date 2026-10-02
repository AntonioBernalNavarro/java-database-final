package com.project.code.Service;

import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.jpa.InventoryRepository;
import com.project.code.Repo.jpa.ProductRepository;

/**
 * Service class that provides validation operations for products and
 * inventory records.
 *
 * <p>This service sits between the controllers and the repository layer.
 * It encapsulates the business rules that determine whether a new product
 * or a new inventory entry may be created, and it exposes a helper method
 * to retrieve an existing inventory record given a product-store pair.</p>
 *
 * <p>The class relies on constructor injection to obtain the two
 * repositories it depends on. This approach favours immutability,
 * simplifies unit testing, and avoids the use of field injection.</p>
 */
@Service
public class ServiceClass {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Constructs a new {@code ServiceClass} with the required repositories.
     *
     * @param productRepository   repository used to query and validate products
     * @param inventoryRepository repository used to query and validate inventory entries
     */
    public ServiceClass(ProductRepository productRepository,
                        InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Verifies whether an inventory record can be created for the given
     * product-store combination.
     *
     * <p>An inventory entry is considered valid when no other entry with
     * the same product and store already exists. If such an entry exists,
     * the method returns {@code false} because creating a duplicate would
     * violate the uniqueness of the product-store pair.</p>
     *
     * @param inventory the inventory entry to validate
     * @return {@code true} if the inventory entry does not yet exist,
     *         {@code false} otherwise
     */
    public boolean validateInventory(Inventory inventory) {
        Long productId = inventory.getProduct().getId();
        Long storeId = inventory.getStore().getId();

        Inventory existing = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        return existing == null;
    }

    /**
     * Verifies whether a product can be created based on its name.
     *
     * <p>Two products with the same name are not allowed in the system.
     * If a product with the same name already exists, the method returns
     * {@code false} to prevent the duplication.</p>
     *
     * @param product the product to validate
     * @return {@code true} if no product with the same name exists,
     *         {@code false} otherwise
     */
    public boolean validateProduct(Product product) {
        Product existing = productRepository.findByName(product.getName());
        return existing == null;
    }

    /**
     * Verifies whether a product with the given identifier exists in the
     * persistence store.
     *
     * <p>This validation is used by higher layers before attempting to
     * perform operations that require the product to be present, such as
     * creating an order item or querying the associated inventory.</p>
     *
     * @param id the identifier of the product to validate
     * @return {@code true} if the product exists, {@code false} otherwise
     */
    public boolean validateProductId(long id) {
        return productRepository.findById(id).isPresent();
    }

    /**
     * Retrieves the inventory record that links the given product with
     * the given store.
     *
     * <p>This helper method is used by other services and controllers
     * that need to inspect or modify the stock level of a specific
     * product at a specific store.</p>
     *
     * @param inventory the inventory entry whose product and store
     *                  identify the record to retrieve
     * @return the matching inventory record, or {@code null} if none exists
     */
    public Inventory getInventoryId(Inventory inventory) {
        Long productId = inventory.getProduct().getId();
        Long storeId = inventory.getStore().getId();

        return inventoryRepository.findByProductIdandStoreId(productId, storeId);
    }
}
