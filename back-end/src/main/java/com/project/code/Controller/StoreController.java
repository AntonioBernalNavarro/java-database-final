package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.jpa.StoreRepository;
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

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    /**
     * Constructs a new {@code StoreController} with the required
     * collaborators.
     *
     * @param storeRepository repository used to persist and query stores
     * @param orderService    service that encapsulates the order process
     */
    public StoreController(StoreRepository storeRepository, OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

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
        Map<String, String> response = new HashMap<>();
        try {
            Store saved = storeRepository.save(store);
            response.put("message", "Store created successfully with id: " + saved.getId());
        } catch (Exception exception) {
            response.put("message", "Error creating store: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Verifies whether a store with the given identifier exists.
     *
     * @param storeId the identifier of the store to validate
     * @return {@code true} if the store exists, {@code false} otherwise
     */
    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.findById(storeId).isPresent();
    }

    /**
     * Places a customer order.
     *
     * <p>The request body describes the customer, the target store, the
     * list of purchased products and the total price. The processing of
     * the order is delegated to {@link OrderService#saveOrder}, which
     * persists the order header, decrements inventory and creates the
     * corresponding order items within a single transaction.</p>
     *
     * @param placeOrderRequest the request describing the order
     * @return a map with either a {@code message} key on success or an
     *         {@code error} key if the process fails
     */
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            response.put("message", "Order placed successfully");
        } catch (Exception exception) {
            response.put("error", exception.getMessage());
        }
        return response;
    }
}
