create table member_task_relation(
    projectmember_id INT REFERENCES projectMembers(projectmember_id),
    task_id INT REFERENCES task(task_id)
)