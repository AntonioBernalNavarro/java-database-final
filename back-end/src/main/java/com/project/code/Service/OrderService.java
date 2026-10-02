package com.project.code.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Product;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.jpa.CustomerRepository;
import com.project.code.Repo.jpa.InventoryRepository;
import com.project.code.Repo.jpa.OrderDetailsRepository;
import com.project.code.Repo.jpa.OrderItemRepository;
import com.project.code.Repo.jpa.ProductRepository;
import com.project.code.Repo.jpa.StoreRepository;

/**
 * Service class that encapsulates the business logic for placing a
 * customer order.
 *
 * <p>Placing an order involves coordinating several repositories: the
 * customer is either retrieved or created, the target store is validated,
 * an order header is persisted, and one order item per purchased product
 * is created while the corresponding inventory levels are decremented.</p>
 *
 * <p>The whole operation is annotated with {@code @Transactional}. This
 * ensures that either every step succeeds and the transaction is
 * committed, or any failure triggers a rollback, leaving the database
 * in a consistent state.</p>
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Constructs a new {@code OrderService} with the required repositories.
     *
     * @param productRepository      repository for product entities
     * @param inventoryRepository    repository for inventory entities
     * @param customerRepository     repository for customer entities
     * @param storeRepository        repository for store entities
     * @param orderDetailsRepository repository for order header entities
     * @param orderItemRepository    repository for order item entities
     */
    public OrderService(ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        CustomerRepository customerRepository,
                        StoreRepository storeRepository,
                        OrderDetailsRepository orderDetailsRepository,
                        OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Processes a customer order end to end.
     *
     * <p>The method follows these steps:</p>
     * <ol>
     *   <li>Retrieves the customer by email or creates a new one if none
     *       exists.</li>
     *   <li>Retrieves the store by identifier, throwing an exception if
     *       the store does not exist.</li>
     *   <li>Creates and persists an {@link OrderDetails} header with the
     *       customer, the store, the total price and the current timestamp.</li>
     *   <li>Iterates over the list of purchased products: for each one, it
     *       decrements the inventory stock level, persists the updated
     *       inventory, and creates an {@link OrderItem} associated with
     *       the order header.</li>
     * </ol>
     *
     * @param placeOrderRequest the incoming request describing the order
     * @throws RuntimeException if the store does not exist, or if the
     *                          inventory entry for a given product and
     *                          store cannot be found
     */
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 1. Retrieve or create the customer.
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // 2. Retrieve the store, or fail if it does not exist.
        Long storeId = placeOrderRequest.getStoreId();
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException(
                "Store not found with id: " + storeId));

        // 3. Create and persist the order header.
        OrderDetails order = new OrderDetails(
            customer,
            store,
            placeOrderRequest.getTotalPrice(),
            LocalDateTime.now()
        );
        order = orderDetailsRepository.save(order);

        // 4. Process each purchased product.
        List<PurchaseProductDTO> products = placeOrderRequest.getPurchaseProduct();
        if (products == null || products.isEmpty()) {
            return;
        }

        for (PurchaseProductDTO dto : products) {

            Long productId = dto.getId();
            Integer quantity = dto.getQuantity();

            // 4a. Retrieve the product, or fail if it does not exist.
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(
                    "Product not found with id: " + productId));

            // 4b. Retrieve the inventory entry for this product at the store.
            Inventory inventory = inventoryRepository
                .findByProductIdandStoreId(productId, storeId);
            if (inventory == null) {
                throw new RuntimeException(
                    "Inventory not found for product id: " + productId
                        + " and store id: " + storeId);
            }

            // 4c. Decrement the stock level and persist the change.
            int updatedStock = inventory.getStockLevel() - quantity;
            inventory.setStockLevel(updatedStock);
            inventoryRepository.save(inventory);

            // 4d. Create and persist the order item.
            OrderItem orderItem = new OrderItem(
                order,
                product,
                quantity,
                dto.getPrice()
            );
            orderItemRepository.save(orderItem);
        }
    }
}
