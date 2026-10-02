package com.project.code.Repo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Store;

/**
 * Repository interface for {@link Store} entities.
 *
 * <p>Extending {@code JpaRepository} provides the full set of standard CRUD
 * operations (save, delete, findById, findAll, etc.) without requiring any
 * manual implementation. In addition to the inherited operations, this
 * repository declares a custom query method for case-insensitive substring
 * searches on the store name.</p>
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

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
    @Query("SELECT s FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Store> findBySubName(@Param("pname") String pname);
}
