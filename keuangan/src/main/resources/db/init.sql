-- ==========================================
-- 1. DROP TABLES (RESET)
-- ==========================================
-- Hapus tabel dengan urutan terbalik dari dependensinya
-- CASCADE memastikan semua relasi ikut terhapus
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==========================================
-- 2. TABEL USERS
-- ==========================================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index username untuk mempercepat proses login
CREATE INDEX idx_users_username ON users(username);


-- ==========================================
-- 3. TABEL CATEGORIES
-- ==========================================
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Index agar cepat saat meload list kategori milik user tertentu
CREATE INDEX idx_categories_user ON categories(user_id);


-- ==========================================
-- 4. TABEL BUDGETS (LIMIT)
-- ==========================================
CREATE TABLE budgets (
    budget_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    category_id INT REFERENCES categories(category_id) ON DELETE CASCADE,
    amount DECIMAL(15, 2) NOT NULL,
    
    -- Menggunakan Rentang Tanggal
    budget_date DATE DEFAULT CURRENT_DATE, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

-- Index Penting untuk Reporting:
-- Mempercepat pencarian budget user X di rentang tanggal Y
CREATE INDEX idx_budgets_lookup ON budgets(user_id, start_date, end_date);


-- ==========================================
-- 5. TABEL TRANSACTIONS (PENGELUARAN)
-- ==========================================
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    category_id INT REFERENCES categories(category_id) ON DELETE SET NULL,
    amount DECIMAL(15, 2) NOT NULL, -- Disamakan tipe datanya dengan budget (DECIMAL)
    description TEXT,
    transaction_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index Penting untuk Statistik:
-- 1. Mempercepat filter transaksi per user per tanggal (Statistik harian/bulanan)
CREATE INDEX idx_transactions_filter ON transactions(user_id, transaction_date);

-- 2. Mempercepat join ke kategori (Untuk laporan pengeluaran per kategori)
CREATE INDEX idx_transactions_category ON transactions(category_id);

