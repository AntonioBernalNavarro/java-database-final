-- ============================================================
-- Stored procedures for sales reporting
-- Target database: store_management
-- ============================================================

USE store_management;

-- Drop existing procedures to allow re-execution
DROP PROCEDURE IF EXISTS GetMonthlySalesForEachStore;
DROP PROCEDURE IF EXISTS GetAggregateSalesForCompany;
DROP PROCEDURE IF EXISTS GetTopSellingProductsByCategory;
DROP PROCEDURE IF EXISTS GetTopSellingProductByStore;

DELIMITER //

-- ------------------------------------------------------------
-- Monthly sales for each store
-- ------------------------------------------------------------
CREATE PROCEDURE GetMonthlySalesForEachStore(
    IN year_param INT,
    IN month_param INT
)
BEGIN
SELECT
    od.store_id AS store_id,
    SUM(DISTINCT od.total_price) AS total_sales,
    MONTH(od.date) AS sale_month,
    YEAR(od.date) AS sale_year
FROM order_details od
WHERE YEAR(od.date) = year_param
  AND MONTH(od.date) = month_param
GROUP BY od.store_id, MONTH(od.date), YEAR(od.date)
ORDER BY total_sales DESC;
END //

-- ------------------------------------------------------------
-- Aggregate sales for the whole company
-- ------------------------------------------------------------
CREATE PROCEDURE GetAggregateSalesForCompany(
    IN year_param INT,
    IN month_param INT
)
BEGIN
SELECT
    SUM(DISTINCT od.total_price) AS total_sales,
    MONTH(od.date) AS sale_month,
    YEAR(od.date) AS sale_year
FROM order_details od
WHERE YEAR(od.date) = year_param
  AND MONTH(od.date) = month_param
GROUP BY MONTH(od.date), YEAR(od.date);
END //

-- ------------------------------------------------------------
-- Top-selling products by category
-- ------------------------------------------------------------
CREATE PROCEDURE GetTopSellingProductsByCategory(
    IN target_month INT,
    IN target_year INT
)
BEGIN
SELECT
    p.category AS category,
    p.name AS name,
    SUM(oi.quantity) AS total_quantity_sold,
    SUM(oi.quantity * oi.price) AS total_sales
FROM order_item oi
         JOIN order_details od ON oi.order_id = od.id
         JOIN product p ON oi.product_id = p.id
WHERE MONTH(od.date) = target_month
  AND YEAR(od.date) = target_year
GROUP BY p.category, p.name
HAVING SUM(oi.quantity) = (
    SELECT MAX(sub.total_qty)
    FROM (
    SELECT SUM(oi2.quantity) AS total_qty
    FROM order_item oi2
    JOIN order_details od2 ON oi2.order_id = od2.id
    JOIN product p2 ON oi2.product_id = p2.id
    WHERE p2.category = p.category
   AND MONTH(od2.date) = target_month
   AND YEAR(od2.date) = target_year
    GROUP BY p2.name
    ) sub
    )
ORDER BY p.category, p.name;
END //

-- ------------------------------------------------------------
-- Top-selling product by store
-- ------------------------------------------------------------
CREATE PROCEDURE GetTopSellingProductByStore(
    IN target_month INT,
    IN target_year INT
)
BEGIN
SELECT
    p.name AS product_name,
    od.store_id AS store_id,
    SUM(oi.quantity) AS total_quantity_sold,
    SUM(oi.quantity * oi.price) AS total_sales
FROM order_item oi
         JOIN order_details od ON oi.order_id = od.id
         JOIN product p ON oi.product_id = p.id
WHERE MONTH(od.date) = target_month
  AND YEAR(od.date) = target_year
GROUP BY od.store_id, p.name
HAVING SUM(oi.quantity) = (
    SELECT MAX(sub.total_qty)
    FROM (
    SELECT SUM(oi2.quantity) AS total_qty
    FROM order_item oi2
    JOIN order_details od2 ON oi2.order_id = od2.id
    WHERE od2.store_id = od.store_id
   AND MONTH(od2.date) = target_month
   AND YEAR(od2.date) = target_year
    GROUP BY oi2.product_id
    ) sub
    )
ORDER BY od.store_id, p.name;
END //

DELIMITER ;
