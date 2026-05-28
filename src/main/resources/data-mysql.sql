-- 初期管理者（パスワード: "changeme"、初回ログイン時に変更必須）
INSERT IGNORE INTO staffs (name, email, password_hash, role, is_active, force_password_change)
VALUES ('管理者', 'admin@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN', TRUE, TRUE);
