INSERT INTO USERS (name, email)
VALUES ('Alice', 'alice@example.com');
INSERT INTO USERS (name, email)
VALUES ('Bob', 'bob@example.com');
INSERT INTO USERS (name, email)
VALUES ('Charlie', 'charlie@example.com');

INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Request for a laptop', 1, CURRENT_TIMESTAMP);
INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Need a projector for presentation', 2, CURRENT_TIMESTAMP);
INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Looking for a camera', 3, CURRENT_TIMESTAMP);

INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Laptop', 'Dell XPS 15', TRUE, 1, 1);
INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Projector', 'Epson EB-U05', TRUE, 2, 2);
INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Camera', 'Canon EOS 5D', TRUE, 3, 3);


INSERT INTO USERS (name, email)
VALUES ('David', 'david@example.com');
INSERT INTO USERS (name, email)
VALUES ('Emily', 'emily@example.com');
INSERT INTO USERS (name, email)
VALUES ('Frank', 'frank@example.com');

INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Request for a microphone', 4, CURRENT_TIMESTAMP);
INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Need a whiteboard for brainstorming', 5, CURRENT_TIMESTAMP);
INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Looking for a projector', 6, CURRENT_TIMESTAMP);

INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Microphone', 'Shure SM58', TRUE, 4, 4);
INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Whiteboard', 'Large magnetic whiteboard', TRUE, 5, 5);
INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Projector', 'Optoma HD143X', TRUE, 6, 6);


INSERT INTO BOOKINGS (start_time, end_time, item_id, booker_id, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 1, 'WAITING');
INSERT INTO BOOKINGS (start_time, end_time, item_id, booker_id, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2, 'APPROVED');
INSERT INTO BOOKINGS (start_time, end_time, item_id, booker_id, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, 3, 'WAITING');
INSERT INTO USERS (name, email)
VALUES ('Alice', 'alice@example.com'),
       ('Bob', 'bob@example.com'),
       ('Charlie', 'charlie@example.com');

INSERT INTO ITEM_REQUESTS (description, requestor_id, created)
VALUES ('Request for a laptop', 1, CURRENT_TIMESTAMP),
       ('Need a projector for presentation', 2, CURRENT_TIMESTAMP),
       ('Looking for a camera', 3, CURRENT_TIMESTAMP);

INSERT INTO ITEMS (name, description, is_available, owner_id, request_id)
VALUES ('Laptop', 'Dell XPS 15', TRUE, 1, 1),
       ('Projector', 'Epson EB-U05', TRUE, 2, 2),
       ('Camera', 'Canon EOS 5D', TRUE, 3, 3);

INSERT INTO BOOKINGS (start_time, end_time, item_id, booker_id, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 1, 2, 'WAITING'),
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 day', 2, 3, 'APPROVED'),
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3 day', 3, 1, 'WAITING');
