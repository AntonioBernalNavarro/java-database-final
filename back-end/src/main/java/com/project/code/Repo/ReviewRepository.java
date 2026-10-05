package com.project.code.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Review;

/**
 * Repository interface for {@link Review} documents.
 *
 * <p>Extending {@code MongoRepository} provides the full set of standard
 * CRUD operations against the MongoDB {@code reviews} collection. The
 * identifier type is {@code String} because MongoDB generates ObjectIds
 * that are represented as hexadecimal strings.</p>
 *
 * <p>In addition to the inherited operations, this repository declares
 * a custom query method that retrieves reviews by store and product
 * identifiers, following the Spring Data derived-query naming
 * convention.</p>
 */
@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    /**
     * Retrieves all reviews associated with a specific product within a
     * specific store.
     *
     * <p>The method relies on Spring Data's derived-query naming
     * convention. The field names {@code storeId} and {@code productId}
     * are matched directly against the properties declared in the
     * {@link Review} document, generating an equivalent MongoDB query
     * without requiring a custom implementation.</p>
     *
     * @param storeId   the identifier of the store
     * @param productId the identifier of the product
     * @return the list of reviews that match both criteria
     */
    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);
}
