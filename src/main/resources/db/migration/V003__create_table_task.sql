CREATE TABLE task(
task_id serial primary key,
project_id INT REFERENCES projects(project_id),
task_name VARCHAR(50),
task_description VARCHAR(2000),
due_date VARCHAR(15),
status VARCHAR(100)
)