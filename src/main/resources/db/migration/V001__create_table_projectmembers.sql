CREATE TABLE projectmembers(
  projectmember_id serial PRIMARY KEY,
  email_address VARCHAR(50),
  first_name VARCHAR(30),
  last_name VARCHAR(50),
  hashed_pw varchar(50)
  )