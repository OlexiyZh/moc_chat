CREATE TABLE users(
    user_id varchar(255) PRIMARY KEY,
    user_name VARCHAR (255) UNIQUE NOT NULL,
    password VARCHAR (255) NOT NULL
);

CREATE TABLE rooms(
    room_id varchar(255) PRIMARY KEY,
    room_name VARCHAR (255) UNIQUE NOT NULL,
    creator_id VARCHAR (255) NOT NULL REFERENCES users(user_id),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE users_to_rooms(
    user_id varchar(255) NOT NULL REFERENCES users(user_id),
    room_id VARCHAR (255) NOT NULL REFERENCES rooms(room_id),
    PRIMARY KEY (user_id, room_id)
);