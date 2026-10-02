package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.jpa.CustomerRepository;
import com.project.code.Repo.mongo.ReviewRepository;

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

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    /**
     * Constructs a new {@code ReviewController} with the required
     * repositories.
     *
     * @param reviewRepository   repository for MongoDB review documents
     * @param customerRepository repository for relational customer entities
     */
    public ReviewController(ReviewRepository reviewRepository,
                            CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

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
    public Map<String, Object> getReviews(@PathVariable Long storeId,
                                          @PathVariable Long productId) {

        Map<String, Object> response = new HashMap<>();

        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        List<Map<String, Object>> enrichedReviews = new ArrayList<>();

        for (Review review : reviews) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("comment", review.getComment());
            entry.put("rating", review.getRating());

            Customer customer = customerRepository.findById(review.getCustomerId()).orElse(null);
            entry.put("customerName", customer != null ? customer.getName() : "Unknown");

            enrichedReviews.add(entry);
        }

        response.put("reviews", enrichedReviews);
        return response;
    }
}
