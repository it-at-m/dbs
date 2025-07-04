create table checklist (
    lhm_ext_id varchar(255) not null,
    title varchar(255) not null,
    last_update timestamp with time zone,
    id uuid not null,
    primary key (id)
);

create table checklist_item (
    service_id varchar(255) not null,
    checked timestamp with time zone,
    title varchar(255),
    note text,
    required boolean,
    checklist_id uuid not null,
    primary key (checklist_id, service_id),
    constraint fk_checklist
        foreign key (checklist_id)
            references checklist(id)
            on delete cascade
);