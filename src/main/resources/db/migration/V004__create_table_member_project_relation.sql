CREATE TABLE member_project_relation(
projectmember_id INT REFERENCES projectMembers(projectmember_id),
project_id INT REFERENCES projects(project_id)
);
