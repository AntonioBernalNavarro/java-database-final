package com.project.code.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a product offered by the store management system.
 *
 * <p>A product is a commercial item that may be stored in the inventory
 * of one or more stores and may be included as a line item within
 * customer orders. Each product is uniquely identified by its stock
 * keeping unit (SKU), which is enforced as a database-level unique
 * constraint.</p>
 */
@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {

    /**
     * Unique identifier of the product. It is automatically generated
     * by the database using an identity-based strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Commercial name of the product. This field is mandatory and must
     * not be left empty.
     */
    @NotNull(message = "Name cannot be null")
    private String name;

    /**
     * Category to which the product belongs (e.g. Mobile, TV and AV,
     * Home Appliances). This field is mandatory.
     */
    @NotNull(message = "Category cannot be null")
    private String category;

    /**
     * Retail price of the product. This field is mandatory.
     */
    @NotNull(message = "Price cannot be null")
    private Double price;

    /**
     * Stock keeping unit (SKU) of the product. This field is mandatory
     * and its value must be unique across all products in the system.
     */
    @NotNull(message = "SKU cannot be null")
    private String sku;

    /**
     * Collection of inventory entries that reference this product.
     *
     * <p>The relationship is bidirectional: the {@code mappedBy}
     * attribute indicates that the {@code product} field in
     * {@link Inventory} owns the foreign key. The
     * {@code @JsonManagedReference} annotation, identified by the value
     * {@code "inventory-product"}, ensures that this side of the
     * relationship is serialised, while the matching
     * {@code @JsonBackReference} on the {@link Inventory} side prevents
     * infinite recursion during JSON serialisation.</p>
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventory;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Product() {
    }

    /**
     * Constructs a product with the given name, category, price and SKU.
     *
     * @param name     the commercial name of the product
     * @param category the category to which the product belongs
     * @param price    the retail price of the product
     * @param sku      the stock keeping unit of the product
     */
    public Product(String name, String category, Double price, String sku) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.sku = sku;
    }

    /**
     * Retrieves the product identifier.
     *
     * @return the unique identifier of the product
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the product identifier.
     *
     * @param id the unique identifier to assign
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the commercial name of the product.
     *
     * @return the name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the commercial name of the product.
     *
     * @param name the name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the category of the product.
     *
     * @return the category of the product
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     *
     * @param category the category to assign
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the retail price of the product.
     *
     * @return the price of the product
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the retail price of the product.
     *
     * @param price the price to assign
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Retrieves the stock keeping unit of the product.
     *
     * @return the SKU of the product
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit of the product.
     *
     * @param sku the SKU to assign
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Retrieves the list of inventory entries that reference this product.
     *
     * @return the list of inventory entries for the product
     */
    public List<Inventory> getInventory() {
        return inventory;
    }

    /**
     * Sets the list of inventory entries that reference this product.
     *
     * @param inventory the list of inventory entries to assign
     */
    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    /**
     * Returns a string representation of the product.
     *
     * @return a textual representation of this product
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", sku='" + sku + '\'' +
                '}';
    }
}