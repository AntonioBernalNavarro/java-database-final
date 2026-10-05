package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

/**
 * REST controller that exposes endpoints for product management.
 *
 * <p>The controller supports the full lifecycle of a product: creating,
 * listing, retrieving, updating and deleting products. It also provides
 * filtered and pattern-based searches used by the frontend to build
 * inventory and catalogue views.</p>
 *
 * <p>Because products are referenced by inventory entries and order items
 * through foreign keys, deletion is performed in three steps: first the
 * order items are removed, then the inventory entries, and finally the
 * product itself.</p>
 */
@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

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

        Map<String, String> map = new HashMap<>();
        if (!serviceClass.validateProduct(product)) {
            map.put("message", "Product already present in database");
            return map;
        }
        try {
            productRepository.save(product);
            map.put("message", "Product added successfully");
        } catch (DataIntegrityViolationException e) {
            map.put("message", "SKU should be unique");
        }
        return map;
    }

    /**
     * Retrieves a product by its identifier.
     *
     * <p>This endpoint uses the URL path {@code /product/product/{id}}
     * as expected by the frontend.</p>
     *
     * @param id the identifier of the product
     * @return a map with a single {@code products} key whose value is the
     *         matching product
     */
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        System.out.println("result: ");
        System.out.println("result: ");
        System.out.println("result: ");
        Map<String, Object> map = new HashMap<>();
        Product result = productRepository.findByid(id);

        System.out.println("result: " + result);
        map.put("products", result);
        return map;
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
        Map<String, String> map = new HashMap<>();
        try {
            productRepository.save(product);
            map.put("message", "Data upated sucessfully");
        } catch (Error e) {
            map.put("message", "Error occured");
        }

        return map;
    }

    /**
     * Filters products by name pattern and category.
     *
     * <p>The special string {@code "null"} may be supplied for either
     * parameter to indicate that the corresponding filter should be
     * ignored. Three combinations are supported:</p>
     * <ul>
     *   <li>Only category: filters by exact category.</li>
     *   <li>Only name pattern: filters by name using a case-insensitive
     *       substring match.</li>
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
        Map<String, Object> map = new HashMap<>();

        if (name.equals("null")) {
            map.put("products", productRepository.findByCategory(category));
            return map;
        } else if (category.equals("null")) {
            map.put("products", productRepository.findProductBySubName(name));
            return map;
        }
        map.put("products", productRepository.findProductBySubNameAndCategory(name, category));
        return map;
    }

    /**
     * Retrieves all products in the catalogue.
     *
     * @return a map with a single {@code products} key whose value is the
     *         list of all products
     */
    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findAll());
        return map;
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
                                                              @PathVariable long storeid) {
        Map<String, Object> map = new HashMap<>();
        List<Product> result = productRepository.findProductByCategory(category, storeid);

        map.put("product", result);
        return map;
    }

    /**
     * Removes a product from the catalogue.
     *
     * <p>The method first checks that the product exists. If so, it
     * removes any associated order items and inventory entries before
     * deleting the product itself, respecting the foreign key
     * constraints that link those tables to the product.</p>
     *
     * @param id the identifier of the product to remove
     * @return a map with a single {@code message} key describing the
     *         outcome
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return map;
        }
        inventoryRepository.deleteByProductId(id);
        orderItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        map.put("message", "Deleted product successfully with id: " + id);
        return map;
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
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findProductBySubName(name));
        return map;
    }
}