package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.jpa.InventoryRepository;
import com.project.code.Repo.jpa.ProductRepository;
import com.project.code.Service.ServiceClass;

/**
 * REST controller that exposes endpoints for inventory management.
 *
 * <p>The controller coordinates the {@link ProductRepository} and the
 * {@link InventoryRepository} to offer endpoints for creating, updating,
 * retrieving and removing inventory entries. It also delegates business
 * validations to the {@link ServiceClass}, which centralizes the checks
 * that determine whether a product or an inventory entry may be created
 * or modified.</p>
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    /**
     * Constructs a new {@code InventoryController} with the required
     * collaborators.
     *
     * @param productRepository   repository for product entities
     * @param inventoryRepository repository for inventory entities
     * @param serviceClass        service providing validation operations
     */
    public InventoryController(ProductRepository productRepository,
                               InventoryRepository inventoryRepository,
                               ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    /**
     * Updates an existing product and its associated inventory entry.
     *
     * <p>The request carries a {@link CombinedRequest} that groups the
     * updated {@link Product} and the {@link Inventory} to modify. The
     * product identifier is validated first; if the product does not
     * exist, no change is performed. Otherwise, the product is persisted
     * and the inventory entry is looked up by product and store. If the
     * inventory exists, its stock level is updated; if it does not, a
     * message indicating the absence of data is returned.</p>
     *
     * @param combinedRequest the request with the product and inventory
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            Product product = combinedRequest.getProduct();
            long productId = product.getId();

            if (!serviceClass.validateProductId(productId)) {
                response.put("message", "Product not present in database");
                return response;
            }

            productRepository.save(product);

            Inventory inventory = combinedRequest.getInventory();
            Long storeId = inventory.getStore().getId();

            Inventory existing = inventoryRepository.findByProductIdandStoreId(productId, storeId);
            if (existing != null) {
                existing.setStockLevel(inventory.getStockLevel());
                inventoryRepository.save(existing);
                response.put("message", "Successfully updated product");
            } else {
                response.put("message", "No data available");
            }
        } catch (DataIntegrityViolationException exception) {
            response.put("message", "Data integrity violation: " + exception.getMessage());
        } catch (Exception exception) {
            response.put("message", "Error updating inventory: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Creates a new inventory entry for a given product-store pair.
     *
     * <p>Before persisting the entry, the method checks whether an
     * inventory record already exists for the same product and store. If
     * it does, the operation is rejected to avoid duplicates.</p>
     *
     * @param inventory the inventory entry to persist
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Data already present");
                return response;
            }
            inventoryRepository.save(inventory);
            response.put("message", "Data saved successfully");
        } catch (DataIntegrityViolationException exception) {
            response.put("message", "Data integrity violation: " + exception.getMessage());
        } catch (Exception exception) {
            response.put("message", "Error saving inventory: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Retrieves all products available in a given store.
     *
     * @param storeid the identifier of the store
     * @return a map with a single {@code products} key whose value is the
     *         list of products available in the store
     */
    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeid);
        response.put("products", products);
        return response;
    }

    /**
     * Retrieves products for a given store filtered by category and name.
     *
     * <p>The filter logic is conditional: the special string {@code "null"}
     * may be used as a value for either {@code category} or {@code name}
     * to indicate that the corresponding filter should be ignored. Three
     * combinations are supported: only category, only name, or both.</p>
     *
     * @param category the category to filter by, or the literal {@code "null"}
     * @param name     the name pattern to filter by, or the literal {@code "null"}
     * @param storeid  the identifier of the store
     * @return a map with a single {@code product} key whose value is the
     *         list of matching products
     */
    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category,
                                              @PathVariable String name,
                                              @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equals(category)) {
            products = productRepository.findByNameLike(storeid, name);
        } else if ("null".equals(name)) {
            products = productRepository.findByCategoryAndStoreId(storeid, category);
        } else {
            products = productRepository.findByNameAndCategory(storeid, name, category);
        }

        response.put("product", products);
        return response;
    }

    /**
     * Searches for products by name pattern within a given store.
     *
     * @param name    the name pattern to search for
     * @param storeId the identifier of the store
     * @return a map with a single {@code product} key whose value is the
     *         list of matching products
     */
    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name,
                                             @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);
        return response;
    }

    /**
     * Removes a product from the inventory of all stores.
     *
     * <p>The method validates the existence of the product before
     * performing any deletion. Because the inventory table holds a foreign
     * key that references the product, the inventory entries must be
     * removed before the product itself. The product is not deleted from
     * the catalogue; only its inventory entries are.</p>
     *
     * @param id the identifier of the product to remove from inventory
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProductId(id)) {
                response.put("message", "Product not present in database");
                return response;
            }
            inventoryRepository.deleteByProductId(id);
            response.put("message", "Product removed from inventory successfully");
        } catch (Exception exception) {
            response.put("message", "Error removing product from inventory: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Verifies whether a given quantity of a product is available in the
     * inventory of a specific store.
     *
     * @param quantity  the quantity to check
     * @param storeId   the identifier of the store
     * @param productId the identifier of the product
     * @return {@code true} if the current stock level is greater than or
     *         equal to the requested quantity, {@code false} otherwise
     */
    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity,
                                    @PathVariable Long storeId,
                                    @PathVariable Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory == null || inventory.getStockLevel() == null) {
            return false;
        }
        return inventory.getStockLevel() >= quantity;
    }
}
