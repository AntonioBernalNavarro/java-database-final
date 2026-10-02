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

import com.project.code.Model.Product;
import com.project.code.Repo.jpa.InventoryRepository;
import com.project.code.Repo.jpa.ProductRepository;
import com.project.code.Service.ServiceClass;

/**
 * REST controller that exposes endpoints for product management.
 *
 * <p>The controller supports the full lifecycle of a product: creating,
 * listing, retrieving, updating and deleting products. It also provides
 * filtered and pattern-based searches used by the frontend to build
 * inventory and catalogue views.</p>
 *
 * <p>Because products are referenced by inventory entries through a
 * foreign key, deletion is performed in two steps: first the inventory
 * entries are removed, and then the product itself is deleted from the
 * catalogue.</p>
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    /**
     * Constructs a new {@code ProductController} with the required
     * collaborators.
     *
     * @param productRepository   repository for product entities
     * @param inventoryRepository repository for inventory entities
     * @param serviceClass        service providing validation operations
     */
    public ProductController(ProductRepository productRepository,
                             InventoryRepository inventoryRepository,
                             ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    /**
     * Creates a new product in the catalogue.
     *
     * <p>The method checks whether another product with the same name
     * already exists. If so, the operation is rejected. Otherwise, the
     * product is persisted and a confirmation message is returned.</p>
     *
     * @param product the product to persist
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product with this name already exists");
                return response;
            }
            productRepository.save(product);
            response.put("message", "Product added successfully");
        } catch (DataIntegrityViolationException exception) {
            response.put("message", "Data integrity violation: " + exception.getMessage());
        } catch (Exception exception) {
            response.put("message", "Error adding product: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Retrieves a product by its identifier.
     *
     * <p>This endpoint uses the URL path {@code /product/product/{id}}
     * as expected by the frontend.</p>
     *
     * @param id the identifier of the product
     * @return a map with a single {@code products} key whose value is the
     *         matching product, or an empty map if the product does not
     *         exist
     */
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        productRepository.findById(id).ifPresent(product -> response.put("products", product));
        return response;
    }

    /**
     * Updates an existing product.
     *
     * @param product the product with the updated values
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");
        } catch (Exception exception) {
            response.put("message", "Error updating product: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Filters products by name pattern and category.
     *
     * <p>The special string {@code "null"} may be supplied for either
     * parameter to indicate that the corresponding filter should be
     * ignored. Three combinations are supported:</p>
     * <ul>
     *   <li>Only name pattern: filters by name using a case-insensitive
     *       substring match.</li>
     *   <li>Only category: filters by exact category.</li>
     *   <li>Both: combines name pattern and category.</li>
     * </ul>
     *
     * @param name     the name pattern, or the literal {@code "null"}
     * @param category the category, or the literal {@code "null"}
     * @return a map with a single {@code products} key whose value is the
     *         list of matching products
     */
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name,
                                                       @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equals(name)) {
            products = productRepository.findByCategory(category);
        } else if ("null".equals(category)) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        response.put("products", products);
        return response;
    }

    /**
     * Retrieves all products in the catalogue.
     *
     * @return a map with a single {@code products} key whose value is the
     *         list of all products
     */
    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findAll());
        return response;
    }

    /**
     * Retrieves products by category for a specific store.
     *
     * @param category the category to filter by
     * @param storeid  the identifier of the store
     * @return a map with a single {@code product} key whose value is the
     *         list of matching products
     */
    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,
                                                              @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategory(category, storeid);
        response.put("product", products);
        return response;
    }

    /**
     * Removes a product from the catalogue.
     *
     * <p>The method first checks that the product exists. If so, it
     * removes any associated inventory entries before deleting the
     * product itself, respecting the foreign key constraint that links
     * inventory entries to products.</p>
     *
     * @param id the identifier of the product to remove
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProductId(id)) {
                response.put("message", "Product not present in database");
                return response;
            }
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            response.put("message", "Product deleted successfully");
        } catch (Exception exception) {
            response.put("message", "Error deleting product: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Searches for products by name pattern.
     *
     * <p>The match is case-insensitive and matches any product whose name
     * contains the given substring.</p>
     *
     * @param name the name pattern to search for
     * @return a map with a single {@code products} key whose value is the
     *         list of matching products
     */
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return response;
    }
}
