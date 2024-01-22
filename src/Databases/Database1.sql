-- Create a table
CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100),
    salary DECIMAL(10, 2)
);

-- Insert data
INSERT INTO employees (id, name, department, salary) VALUES
(1, 'John Doe', 'Engineering', 75000.00),
(2, 'Jane Smith', 'Marketing', 65000.00);

-- Simple query
SELECT
    name,
    department
FROM employees WHERE salary > 60000;
