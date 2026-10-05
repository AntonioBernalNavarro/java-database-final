package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Store;

/**
 * Repository interface for {@link Store} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard
 * CRUD operations. In addition to the inherited operations, this
 * repository declares custom query methods that support lookups by
 * identifier and by name pattern.</p>
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * Retrieves a store by its identifier.
     *
     * <p>The method is named {@code findByid} (with a lowercase "i")
     * because the specification requires that spelling. Spring Data
     * resolves the derived query against the {@code id} attribute of
     * the entity, so the behaviour is identical to the inherited
     * {@code findById} method except for the return type: this method
     * returns the entity directly, while the inherited one returns an
     * {@code Optional<Store>}.</p>
     *
     * @param id the identifier of the store
     * @return the matching store, or {@code null} if none exists
     */
    Store findByid(Long id);

    /**
     * Retrieves all stores whose name contains the given substring,
     * ignoring case differences.
     *
     * <p>The query uses the {@code LOWER} function on both operands to
     * ensure case-insensitive matching, and {@code CONCAT} to build the
     * pattern with wildcard characters around the input parameter.</p>
     *
     * @param pname the substring to search for within the store name
     * @return the list of stores whose name matches the pattern
     */
    @Query("SELECT p FROM Store p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Store> findBySubName(String pname);
}