DROP TABLE IF EXISTS break;
CREATE TABLE break (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  break_type varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  in_time varchar(255) DEFAULT NULL,
  out_time varchar(255) DEFAULT NULL,
  user_name varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS break_types;
CREATE TABLE break_types (
  id SERIAL NOT NULL,
  break_type varchar(255) DEFAULT NULL,
  campaing_name varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS campaing;
CREATE TABLE campaing (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  active varchar(255) DEFAULT NULL,
  additional_field varchar(255) DEFAULT NULL,
  assignment_type varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  dial_prefix varchar(255) DEFAULT NULL,
  dial_timeout varchar(255) DEFAULT NULL,
  local_call_time varchar(255) DEFAULT NULL,
  manual_dial_prefix varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS campaing_lead_mapping;
CREATE TABLE campaing_lead_mapping (
  id SERIAL NOT NULL,
  campaing_name varchar(255) DEFAULT NULL,
  lead_version_name varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS group_campaing_mapping;
CREATE TABLE group_campaing_mapping (
  id SERIAL NOT NULL,
  campaingname varchar(255) DEFAULT NULL,
  groupname varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS leads;
CREATE TABLE leads (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  assigned_to varchar(255) DEFAULT NULL,
  call_back_date_time varchar(255) DEFAULT NULL,
  call_count SERIAL NOT NULL,
  city varchar(255) DEFAULT NULL,
  comments varchar(255) DEFAULT NULL,
  crm varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  filename varchar(255) DEFAULT NULL,
  first_name varchar(255) DEFAULT NULL,
  last_local_call_time varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  phone_number varchar(255) DEFAULT NULL,
  state varchar(255) DEFAULT NULL,
  status varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS lead_versions;
CREATE TABLE lead_versions (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  campaing_name varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  filename varchar(255) DEFAULT NULL,
  status varchar(255) DEFAULT NULL,
  valid_till datetime DEFAULT NULL
);
DROP TABLE IF EXISTS recordings;
CREATE TABLE recordings (
  id SERIAL NOT NULL,
  recording_name varchar(255) DEFAULT NULL,
  username varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS status;
CREATE TABLE status (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  campaing_name varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  status varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  full_name varchar(255) DEFAULT NULL,
  level SERIAL NOT NULL,
  online varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  status varchar(255) DEFAULT NULL,
  usergroup varchar(255) DEFAULT NULL,
  username varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS user_group;
CREATE TABLE user_group (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  active varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  name varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS user_group_mapping;
CREATE TABLE user_group_mapping (
  id SERIAL NOT NULL,
  groupname varchar(255) DEFAULT NULL,
  username varchar(255) DEFAULT NULL
);
DROP TABLE IF EXISTS user_status_details;
CREATE TABLE user_status_details (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  activity_done varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  last_logged_in_date_time varchar(255) DEFAULT NULL,
  status varchar(255) DEFAULT NULL
);

ALTER TABLE break
  ADD PRIMARY KEY (id);

ALTER TABLE break_types
  ADD PRIMARY KEY (id);

ALTER TABLE campaing
  ADD PRIMARY KEY (id);

ALTER TABLE campaing_lead_mapping
  ADD PRIMARY KEY (id);

ALTER TABLE group_campaing_mapping
  ADD PRIMARY KEY (id);

ALTER TABLE leads
  ADD PRIMARY KEY (id);

ALTER TABLE lead_versions
  ADD PRIMARY KEY (id);

ALTER TABLE recordings
  ADD PRIMARY KEY (id);

ALTER TABLE status
  ADD PRIMARY KEY (id);

ALTER TABLE users
  ADD PRIMARY KEY (id);

ALTER TABLE user_group
  ADD PRIMARY KEY (id);

ALTER TABLE user_group_mapping
  ADD PRIMARY KEY (id);
ALTER TABLE user_status_details
  ADD PRIMARY KEY (id);
COMMIT;