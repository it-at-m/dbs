truncate the_entity CASCADE;
truncate checklist CASCADE;
truncate checklist_item CASCADE;

INSERT INTO the_entity (text_attribute, id) VALUES
('Alpha', '123e4567-e89b-12d3-a456-426614174000'),
('Bravo', '123e4567-e89b-12d3-a456-426614174001'),
('Charlie', '123e4567-e89b-12d3-a456-426614174002'),
('Delta', '123e4567-e89b-12d3-a456-426614174003'),
('Echo', '123e4567-e89b-12d3-a456-426614174004');


INSERT INTO checklist (lhm_ext_id, id) VALUES
('user1', uuid_generate_v4()),
('user2', uuid_generate_v4()),
('user3', uuid_generate_v4()),

INSERT INTO checklist_item (service_id, checked, title, note, required, id, checklist_id) VALUES
('service1', TRUE, 'Item 1', 'Note for Item 1', TRUE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user1'),
('service2', TRUE, 'Item 2', 'Note for Item 2', FALSE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user1'),
('service3', TRUE, 'Item 3', 'Note for Item 3', FALSE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user2'),
('service4', TRUE, 'Item 4', 'Note for Item 4', TRUE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user3'),
('service5', TRUE, 'Item 5', 'Note for Item 5', TRUE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user3'),
('service6', TRUE, 'Item 6', 'Note for Item 6', FALSE, uuid_generate_v4(), (select id from checklist where lhm_ext_id='user3'),