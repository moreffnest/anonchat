delete from message;
delete from user_conversation;
delete from conversation;
delete from user_role;
delete from chatuser;


insert into chatuser(id, username, password, email)
values      (1, 'justuser', '$2a$07$aoK2NyU36YvEwD0zISMhYe/vLoh.OjOqq3ciy4czlUUtQElAqXKyu', 'justuser@gmail.com'),
            (2, 'simpleuser', '$2a$07$I.Weznuihc/rMvbFnpcF3eTquLJPXY/IuDeRBg9OSg.3zaZY0v4mi', 'simpleuser@gmail.com'),
            (3, 'newuser', '$2a$07$Tc1cH8kOzBkmDrGkedaqkehoxphAenk6ibu.u0p8l/6lFtLBDXx9.', 'newuser@gmail.com');

alter sequence chatuser_seq restart with 4;

insert into user_role(user_id, role_id)
values      (1, 1),
            (2, 1),
            (3, 1);

insert into conversation(id, preserved)
values      (1, true);
alter sequence conversation_seq restart with 2;

insert into user_conversation(user_id, conversation_id)
values      (1, 1),
            (3, 1);

insert into message(id, content, sender_username, conversation_id)
values      (1, 'hello!', 'justuser', 1),
            (2, 'hi', 'newuser', 1),
            (3, 'how are you?', 'justuser', 1),
            (4, 'good. bye.', 'newuser', 1);
alter sequence message_seq restart with 5;