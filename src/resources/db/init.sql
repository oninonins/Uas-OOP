-- Tabel Users untuk Fitur Login
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Transaksi untuk Fitur Pengeluaran & Statistik
-- Tipe: 'PEMASUKAN' atau 'PENGELUARAN'
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    amount DECIMAL(15, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    transaction_date DATE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('PEMASUKAN', 'PENGELUARAN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Budget untuk Fitur Budget Bulanan
CREATE TABLE IF NOT EXISTS budgets (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    category VARCHAR(50) NOT NULL,
    limit_amount DECIMAL(15, 2) NOT NULL,
    month INT NOT NULL, 
    year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Username: admin, Password: password123
INSERT INTO users (username, password) VALUES ('admin', 'password123') 
ON CONFLICT (username) DO NOTHING;