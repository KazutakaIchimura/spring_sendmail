-- 初期管理者（パスワード: "changeme"、初回ログイン時に変更必須）
INSERT INTO staffs (name, email, password_hash, role, is_active, force_password_change)
SELECT '管理者', 'admin@example.com',
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
       'ADMIN', TRUE, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM staffs WHERE email = 'admin@example.com'
);

-- offices.building カラムが存在しない場合のみ追加
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'offices'
      AND COLUMN_NAME = 'building'
);
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE offices ADD COLUMN building VARCHAR(200) NULL AFTER postal_code',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
