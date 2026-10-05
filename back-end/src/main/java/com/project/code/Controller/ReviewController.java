package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;

/**
 * REST controller that exposes endpoints for retrieving product reviews.
 *
 * <p>Reviews are stored in MongoDB, while the customer names are kept in
 * the MySQL database. This controller combines both sources: it retrieves
 * the reviews from MongoDB and enriches each one with the name of the
 * customer who authored it, obtained from the relational store.</p>
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    CustomerRepository customerRepository;

    /**
     * Retrieves the reviews of a specific product at a specific store,
     * enriched with the name of the customer who authored each review.
     *
     * <p>The method performs two steps:</p>
     * <ol>
     *   <li>Fetches all reviews that match the given store and product
     *       identifiers from the MongoDB collection.</li>
     *   <li>For each review, looks up the authoring customer in the
     *       relational store and extracts a simplified structure
     *       containing the comment, the rating and the customer name.
     *       If the customer cannot be found, the value {@code "Unknown"}
     *       is used instead.</li>
     * </ol>
     *
     * @param storeId   the identifier of the store
     * @param productId the identifier of the product
     * @return a map with a single {@code reviews} key whose value is the
     *         list of enriched review objects
     */
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable long storeId,
                                          @PathVariable long productId) {
        Map<String, Object> map = new HashMap<>();
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);

        List<Map<String, Object>> reviewsWithCustomerNames = new ArrayList<>();

        // For each review, fetch customer details and add them to the response
        for (Review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("comment", review.getComment());
            reviewMap.put("rating", review.getRating());

            // Fetch customer details using customerId
            Customer customer = customerRepository.findByid(review.getCustomerId());
            if (customer != null) {
                reviewMap.put("customerName", customer.getName());
            } else {
                reviewMap.put("customerName", "Unknown");
            }

            reviewsWithCustomerNames.add(reviewMap);
        }

        map.put("reviews", reviewsWithCustomerNames);
        return map;
    }
}