DROP TABLE IF EXISTS mpa_ratings, films, genres, users, film_likes, film_genre,user_friends, mpa_ratings;
CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
CREATE TABLE IF NOT EXISTS films
(
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR,
    description  VARCHAR,
    duration     INT,
    release_date DATE,
    mpa          INT REFERENCES mpa_ratings (id)
);
CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR,
    login    VARCHAR,
    name     VARCHAR,
    birthday DATE
);
CREATE TABLE IF NOT EXISTS film_likes
(
    film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT film_likes_pk PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES films (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT film_genre_pk PRIMARY KEY (film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS user_friends
(
    user_id    INTEGER REFERENCES users (id) ON DELETE CASCADE,
    friends_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT user_friends_pk PRIMARY KEY (user_id, friends_id)
);
CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
INSERT INTO MPA_RATINGS (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
INSERT INTO GENRES (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');