package com.project.code.Repo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.code.Model.OrderDetails;

/**
 * Repository that exposes the stored procedures used for sales reporting.
 *
 * <p>Each method is bound to a MySQL stored procedure through the
 * {@code @Procedure} annotation. The {@code procedureName} attribute
 * refers to the name of the procedure as it is declared in the database,
 * which may differ from the Java method name. The {@code @Param}
 * annotations map the method arguments to the parameter names declared
 * in the procedure signature.</p>
 *
 * <p>The method names avoid the {@code By} keyword so that IntelliJ does
 * not mistake them for derived query methods. The surrounding transaction
 * is declared at the controller level so that the JDBC connection stays
 * open while the result set is consumed.</p>
 */
@Repository
public interface ReportRepository extends JpaRepository<OrderDetails, Long> {

    @Procedure(procedureName = "GetMonthlySalesForEachStore")
    List<Object[]> monthlySalesForEachStore(@Param("year_param") int year,
                                            @Param("month_param") int month);

    @Procedure(procedureName = "GetAggregateSalesForCompany")
    List<Object[]> aggregateSalesForCompany(@Param("year_param") int year,
                                            @Param("month_param") int month);

    @Procedure(procedureName = "GetTopSellingProductsByCategory")
    List<Object[]> topProductsForCategory(@Param("target_month") int month,
                                          @Param("target_year") int year);

    @Procedure(procedureName = "GetTopSellingProductByStore")
    List<Object[]> topProductForStore(@Param("target_month") int month,
                                      @Param("target_year") int year);
}
