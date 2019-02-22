create table deploy_record
(
  deploy_id     varchar(255) not null
    primary key,
  job_id        varchar(255) null,
  job_name      varchar(255) null,
  node_addr     varchar(255) null,
  node_name     varchar(255) null,
  deploy_status varchar(255) null,
  deploy_time   varchar(255) null
);

create table job_info
(
  job_id       varchar(255) not null
    primary key,
  job_name     varchar(255) null,
  request_mem  varchar(255) null,
  request_cpu  varchar(255) null,
  request_disk varchar(255) null,
  request_io   varchar(255) null
);

create table node_info
(
  node_id   varchar(255) default '' not null
    primary key,
  node_addr varchar(255)            not null,
  node_name varchar(255)            not null,
  node_disk varchar(255)            null,
  node_io   varchar(255)            null,
  node_cpu  varchar(255)            null,
  node_mem  varchar(255)            null
);


