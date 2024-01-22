-- Incorrect table aliasing
SELECT
    u.name,
    o.order_date
FROM users AS u
INNER JOIN orders AS o ON u.id = o.user_id;

-- Incorrect string concatenation
SELECT 'User ID: ' || CAST(id AS VARCHAR) AS user_id_concat
FROM users;

-- Using non-ANSI quotes for strings
SELECT * FROM products WHERE category = electronics;

-- Improper ORDER BY syntax
SELECT * FROM customers ORDER BY id ASC, name DESC;

-- Improperly formatted CASE statement
SELECT
    CASE
        WHEN age < 18 THEN 'Minor'
        ELSE 'Adult'
    END AS age_category
FROM users;

-- Using wrong wildcard character
SELECT * FROM employees WHERE name LIKE '%John%';
