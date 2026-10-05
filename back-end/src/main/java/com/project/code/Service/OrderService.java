package com.project.code.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

/**
 * Service class that encapsulates the business logic for placing a
 * customer order.
 *
 * <p>Placing an order involves coordinating several repositories: the
 * customer is either retrieved or created, the target store is
 * validated, an order header is persisted, and one order item per
 * purchased product is created while the corresponding inventory levels
 * are decremented.</p>
 */
@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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
     *       customer, the store, the total price and the current
     *       timestamp.</li>
     *   <li>Iterates over the list of purchased products: for each one,
     *       it decrements the inventory stock level, persists the updated
     *       inventory, and creates an {@link OrderItem} associated with
     *       the order header.</li>
     * </ol>
     *
     * @param placeOrderRequest the incoming request describing the order
     * @throws RuntimeException if the store does not exist
     */
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 1. Retrieve or create the Customer
        Customer existingCustomer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        Customer customer = new Customer();
        customer.setName(placeOrderRequest.getCustomerName());
        customer.setEmail(placeOrderRequest.getCustomerEmail());
        customer.setPhone(placeOrderRequest.getCustomerPhone());

        if (existingCustomer == null) {
            customer = customerRepository.save(customer);
        } else {
            customer = existingCustomer;
        }

        // 2. Retrieve the Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 3. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());

        orderDetails = orderDetailsRepository.save(orderDetails);

        // 4. Create and save OrderItems (products purchased)
        List<PurchaseProductDTO> purchaseProducts = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO productDTO : purchaseProducts) {
            OrderItem orderItem = new OrderItem();

            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                    productDTO.getId(), placeOrderRequest.getStoreId());

            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
            inventoryRepository.save(inventory);

            orderItem.setOrder(orderDetails);

            orderItem.setProduct(productRepository.findByid(productDTO.getId()));

            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice() * productDTO.getQuantity());

            orderItemRepository.save(orderItem);
        }
    }
}