package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Repo.jpa.ReportRepository;

/**
 * REST controller that exposes the stored procedures used for sales
 * reporting.
 *
 * <p>Each endpoint delegates to a stored procedure in MySQL and returns
 * the resulting rows as a JSON object with a named collection. The
 * {@code buildResponse} helper transforms the raw {@code Object[]} rows
 * returned by the repository into maps with descriptive keys.</p>
 *
 * <p>The class is annotated with {@code @Transactional} without the
 * {@code readOnly} flag. A surrounding transaction is required by Spring
 * Data when invoking {@code @Procedure} methods so that the JDBC
 * connection remains open while the result set is consumed. The
 * {@code readOnly} flag cannot be used here because MySQL Connector/J
 * rejects {@code CALL} statements on read-only connections.</p>
 */
@RestController
@RequestMapping("/report")
@Transactional
public class ReportController {

    private final ReportRepository reportRepository;

    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/monthlySalesByStore/{year}/{month}")
    public Map<String, Object> getMonthlySalesByStore(@PathVariable int year,
                                                      @PathVariable int month) {
        return buildResponse("sales",
            reportRepository.monthlySalesForEachStore(year, month),
            new String[]{"storeId", "totalSales", "saleMonth", "saleYear"});
    }

    @GetMapping("/aggregateSalesByCompany/{year}/{month}")
    public Map<String, Object> getAggregateSalesByCompany(@PathVariable int year,
                                                          @PathVariable int month) {
        return buildResponse("sales",
            reportRepository.aggregateSalesForCompany(year, month),
            new String[]{"totalSales", "saleMonth", "saleYear"});
    }

    @GetMapping("/topProductsByCategory/{month}/{year}")
    public Map<String, Object> getTopProductsByCategory(@PathVariable int month,
                                                        @PathVariable int year) {
        return buildResponse("products",
            reportRepository.topProductsForCategory(month, year),
            new String[]{"category", "name", "totalQuantitySold", "totalSales"});
    }

    @GetMapping("/topProductByStore/{month}/{year}")
    public Map<String, Object> getTopProductByStore(@PathVariable int month,
                                                    @PathVariable int year) {
        return buildResponse("products",
            reportRepository.topProductForStore(month, year),
            new String[]{"productName", "storeId", "totalQuantitySold", "totalSales"});
    }

    private Map<String, Object> buildResponse(String key,
                                              List<Object[]> rows,
                                              String[] columnNames) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                item.put(columnNames[i], row[i]);
            }
            items.add(item);
        }
        Map<String, Object> response = new HashMap<>();
        response.put(key, items);
        return response;
    }
}
