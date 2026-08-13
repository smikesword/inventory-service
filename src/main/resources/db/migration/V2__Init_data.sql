-- 1. Insert some tags
INSERT INTO Tag (key_tag, value) VALUES
                           ('food_type', 'dairy'),
                           ('location','pantry');

-- 2. Insert 2 items
-- (date_bought will automatically default to NOW())
INSERT INTO Item (name, price, expiration_date, quantity, quantity_unit) VALUES
                                                     ('Organic Whole Milk', 4.99, NOW() + INTERVAL '7 days', 5, 'pc'),
                                                     ('Canned Black Beans', 1.25, NULL, 10, 'pc');

-- 3. Link the items to their tags using subqueries
INSERT INTO item_tags (item_id, tag_id)
SELECT i.id, t.id
FROM Item i, Tag t
WHERE i.name = 'Organic Whole Milk' AND t.key_tag = 'food_type' AND t.value = 'dairy';

INSERT INTO item_tags (item_id, tag_id)
SELECT i.id, t.id
FROM Item i, Tag t
WHERE i.name = 'Canned Black Beans' AND t.key_tag = 'location' AND t.value = 'pantry';