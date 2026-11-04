-- PostgreSQL Multi-Database Initialization Script for GST Invoice Generator Microservices
-- Each microservice strictly owns its own dedicated database and never accesses another service's tables.

CREATE DATABASE auth_db;
CREATE DATABASE business_db;
CREATE DATABASE customer_db;
CREATE DATABASE product_db;
CREATE DATABASE gst_db;
CREATE DATABASE invoice_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
CREATE DATABASE report_db;

GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE business_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE customer_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE product_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE gst_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE invoice_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE report_db TO postgres;

