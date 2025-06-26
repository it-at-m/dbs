create table the_entity (
    text_attribute varchar(8) not null,
    id uuid not null,
    primary key (id)
);

create table checklist (
    lhm_ext_id VARCHAR(255) not null,
    last_update TIMESTAMP WITH TIME ZONE,
    id uuid not null,
    primary key (id)
);

create table checklist_item (
    service_id varchar(255) not null,
    checked TIMESTAMP WITH TIME ZONE,
    title VARCHAR(255),
    note TEXT,
    required BOOLEAN,
    id uuid not null,
    checklist_id uuid NOT NULL,
    primary key (id),
    CONSTRAINT fk_checklist
        FOREIGN KEY (checklist_id)
            REFERENCES checklist(id)
            ON DELETE CASCADE
);