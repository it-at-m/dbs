truncate checklist CASCADE;
truncate checklist_item CASCADE;

INSERT INTO checklist (email, title, last_update, id) VALUES
('user1@example.com', 'title1', NOW(), '123e4567-e89b-12d3-a456-426614174000'),
('user2@example.com', 'title2', NOW(), '123e4567-e89b-12d3-a456-426614174001'),
('user3@example.com', 'title3', NOW(), '123e4567-e89b-12d3-a456-426614174002');

INSERT INTO checklist_item (service_id, checked, title, note, required, checklist_id) VALUES
('service1', NOW(), 'Item 1', 'Note for Item 1', TRUE, (select id from checklist where email='user1@example.com')),
('service2', NOW(), 'Item 2', 'Note for Item 2', FALSE, (select id from checklist where email='user1@example.com')),
('service3', NOW(), 'Item 3', 'Note for Item 3', FALSE, (select id from checklist where email='user2@example.com')),
('service4', NOW(), 'Item 4', 'Note for Item 4', TRUE, (select id from checklist where email='user3@example.com')),
('service5', NOW(), 'Item 5', 'Note for Item 5', TRUE, (select id from checklist where email='user3@example.com')),
('service6', NOW(), 'Item 6', 'Note for Item 6', FALSE, (select id from checklist where email='user3@example.com'));