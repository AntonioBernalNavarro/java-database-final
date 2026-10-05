package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;

/**
 * REST controller that exposes endpoints for store management and order
 * placement.
 *
 * <p>This controller handles three responsibilities: registering new
 * stores, validating the existence of a store by identifier, and
 * delegating the processing of a customer order to the
 * {@link OrderService}.</p>
 */
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Registers a new store in the system.
     *
     * <p>The request body must contain the store name and address. Once
     * the store is persisted, the response includes a success message
     * with the identifier assigned by the database.</p>
     *
     * @param store the store to persist
     * @return a map with a single {@code message} key describing the
     *         outcome of the operation
     */
    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Store savedStore = storeRepository.save(store);
        Map<String, String> map = new HashMap<>();
        map.put("message", "Store added successfully with id " + savedStore.getId());
        return map;
    }

    /**
     * Verifies whether a store with the given identifier exists.
     *
     * @param storeId the identifier of the store to validate
     * @return {@code true} if the store exists, {@code false} otherwise
     */
    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        Store store = storeRepository.findByid(storeId);
        if (store != null) {
            return true;
        }
        return false;
    }

    /**
     * Places a customer order.
     *
     * <p>The request body describes the customer, the target store, the
     * list of purchased products and the total price. The processing of
     * the order is delegated to {@link OrderService#saveOrder}, which
     * persists the order header, decrements inventory and creates the
     * corresponding order items.</p>
     *
     * @param placeOrderRequest the request describing the order
     * @return a map with either a {@code message} key on success or an
     *         {@code Error} key if the process fails
     */
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {

        Map<String, String> map = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            map.put("message", "Order placed successfully");
        } catch (Error e) {
            map.put("Error", "" + e);
        }
        return map;
    }
}