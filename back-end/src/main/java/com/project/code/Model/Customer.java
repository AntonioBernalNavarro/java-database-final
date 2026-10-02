package com.project.code.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a customer within the store management system.
 *
 * <p>A customer is a person who may place one or more orders across the
 * different stores managed by the application. The relationship with
 * {@link OrderDetails} is modelled as a one-to-many association, meaning
 * that a single customer can be linked to multiple orders, while each
 * order belongs to exactly one customer.</p>
 */
@Entity
public class Customer {

    /**
     * Unique identifier of the customer. It is automatically generated
     * by the database using an identity-based strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Full name of the customer. This field is mandatory and must not
     * be left empty.
     */
    @NotNull(message = "Name cannot be null")
    private String name;

    /**
     * Contact email address of the customer. This field is mandatory
     * and must not be left empty.
     */
    @NotNull(message = "Email cannot be null")
    private String email;

    /**
     * Contact phone number of the customer. This field is mandatory
     * and must not be left empty.
     */
    @NotNull(message = "Phone cannot be null")
    private String phone;

    /**
     * Collection of orders placed by this customer.
     *
     * <p>The relationship is bidirectional: the {@code mappedBy} attribute
     * indicates that the {@code customer} field in {@link OrderDetails}
     * owns the foreign key. The {@code @JsonManagedReference} annotation
     * is used so that the orders are serialised on this side while the
     * corresponding {@code @JsonBackReference} in the child entity
     * prevents infinite recursion during JSON serialisation.</p>
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    @JsonManagedReference("customer-orders")
    private List<OrderDetails> orders = new ArrayList<>();

    /**
     * Default no-argument constructor required by JPA.
     */
    public Customer() {
    }

    /**
     * Retrieves the customer identifier.
     *
     * @return the unique identifier of the customer
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the customer identifier.
     *
     * @param id the unique identifier to assign
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the customer's full name.
     *
     * @return the name of the customer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's full name.
     *
     * @param name the name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the customer's email address.
     *
     * @return the email of the customer
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email the email to assign
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the customer's phone number.
     *
     * @return the phone number of the customer
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the customer's phone number.
     *
     * @param phone the phone number to assign
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retrieves the list of orders associated with this customer.
     *
     * @return the list of orders placed by the customer
     */
    public List<OrderDetails> getOrders() {
        return orders;
    }

    /**
     * Sets the list of orders associated with this customer.
     *
     * @param orders the list of orders to assign
     */
    public void setOrders(List<OrderDetails> orders) {
        this.orders = orders;
    }
}
