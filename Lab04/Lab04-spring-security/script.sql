
INSERT INTO roles (name, active) VALUES
('ADMIN', true),
('USER', true)
ON CONFLICT (name) DO UPDATE SET active = EXCLUDED.active;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'USER' AND p.method = 'GET'
ON CONFLICT DO NOTHING;

select * from role_permissions;
select * from permissions;
