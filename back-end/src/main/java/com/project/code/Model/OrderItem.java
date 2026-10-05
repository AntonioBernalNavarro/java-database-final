package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entity representing a single line item within a customer order.
 *
 * <p>An order item links an {@link OrderDetails} instance with a
 * {@link Product} and records the quantity purchased together with the
 * unit price applied at the moment the order was placed. This design
 * decouples the historical price paid by the customer from the current
 * catalogue price of the product, allowing product prices to change
 * over time without altering the details of past orders.</p>
 */
@Entity
public class OrderItem {

    /**
     * Unique identifier of the order item. It is automatically generated
     * by the database using an identity-based strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Order to which this line item belongs.
     *
     * <p>This field links the line item to the {@link OrderDetails}
     * entity through the foreign key column {@code order_id}. Together
     * with the collection declared on the {@code OrderDetails} side, it
     * forms a bidirectional one-to-many relationship. The
     * {@code @JsonManagedReference} annotation ensures that this side
     * of the relationship is serialised in JSON responses.</p>
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonManagedReference
    private OrderDetails order;

    /**
     * Product that this line item refers to.
     *
     * <p>This field links the line item to the {@link Product} entity
     * through the foreign key column {@code product_id}. The
     * {@code @JsonManagedReference} annotation ensures that this side
     * of the relationship is serialised in JSON responses.</p>
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    /**
     * Quantity of the product purchased within the order.
     */
    private Integer quantity;

    /**
     * Price of the product at the moment the order was placed. This
     * value may differ from the current catalogue price of the product,
     * preserving the historical accuracy of the transaction.
     */
    private Double price;

    /**
     * Default no-argument constructor required by JPA.
     */
    public OrderItem() {
    }

    /**
     * Constructs an order item with the given order, product, quantity
     * and price.
     *
     * @param order    the order to which this line item belongs
     * @param product  the product being purchased
     * @param quantity the quantity of the product
     * @param price    the price applied at the moment of purchase
     */
    public OrderItem(OrderDetails order, Product product, Integer quantity, Double price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Retrieves the order item identifier.
     *
     * @return the unique identifier of the order item
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order item identifier.
     *
     * @param id the unique identifier to assign
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the order to which this line item belongs.
     *
     * @return the parent order
     */
    public OrderDetails getOrder() {
        return order;
    }

    /**
     * Sets the order to which this line item belongs.
     *
     * @param order the parent order to assign
     */
    public void setOrder(OrderDetails order) {
        this.order = order;
    }

    /**
     * Retrieves the product referenced by this line item.
     *
     * @return the product purchased
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product referenced by this line item.
     *
     * @param product the product to assign
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Retrieves the quantity of the product purchased.
     *
     * @return the quantity of the product
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product purchased.
     *
     * @param quantity the quantity to assign
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Retrieves the price of the product at the time of purchase.
     *
     * @return the price applied to this line item
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price of the product at the time of purchase.
     *
     * @param price the price to assign
     */
    public void setPrice(Double price) {
        this.price = price;
    }
}