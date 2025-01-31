-- User data
INSERT INTO public.users (id, name, created_at, updated_at)
VALUES (1, 'mirz', '2025-01-29 13:05:01.000000',  '2025-01-29 13:05:02.000000');

-- Inventory data
INSERT INTO public.inventory (id, product_id, name, available, price, stock, created_at,  updated_at) 
VALUES  (3,'prod-3', 'kaca', true, 2000, 10000, '2025-01-29 13:03:27.000000',  '2025-01-29 13:03:29.000000'),
        (1,'prod-1', 'botol', true, 5000, 10000, '2025-01-29 13:03:27.000000',  '2025-01-31 07:43:42.024000'),
        (2,'prod-2', 'sandal', true, 10000, 10000, '2025-01-29 13:03:27.000000',  '2025-01-31 07:43:42.025000');

