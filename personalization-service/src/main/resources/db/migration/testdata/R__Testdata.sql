truncate checklist CASCADE;
truncate checklist_item CASCADE;

INSERT INTO checklist (lhm_ext_id, title, last_update, id) VALUES
('user1-lhm-ext-id', 'title1', NOW(), '123e4567-e89b-12d3-a456-426614174000'),
('user2-lhm-ext-id', 'title2', NOW(), '123e4567-e89b-12d3-a456-426614174001'),
('user3-lhm-ext-id', 'title3', NOW(), '123e4567-e89b-12d3-a456-426614174002');

INSERT INTO checklist_item (service_id, checked, title, note, required, checklist_id) VALUES
('service1', NOW(), 'Item 1', 'Note for Item 1', TRUE, (select id from checklist where lhm_ext_id='user1-lhm-ext-id')),
('service2', NOW(), 'Item 2', 'Note for Item 2', FALSE, (select id from checklist where lhm_ext_id='user1-lhm-ext-id')),
('service3', NOW(), 'Item 3', 'Note for Item 3', FALSE, (select id from checklist where lhm_ext_id='user2-lhm-ext-id')),
('service4', NOW(), 'Item 4', 'Note for Item 4', TRUE, (select id from checklist where lhm_ext_id='user3-lhm-ext-id')),
('service5', NOW(), 'Item 5', 'Note for Item 5', TRUE, (select id from checklist where lhm_ext_id='user3-lhm-ext-id')),
('service6', NOW(), 'Item 6', 'Note for Item 6', FALSE, (select id from checklist where lhm_ext_id='user3-lhm-ext-id'));