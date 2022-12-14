DROP TABLE IF EXISTS mpa_ratings, films, genres, users, film_likes, film_genre,user_friends, director, film_director,
    reviews, review_likes, review_dislikes, feed;
CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
CREATE TABLE IF NOT EXISTS films
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR,
    description  VARCHAR,
    duration     INT,
    release_date DATE,
    mpa          INT REFERENCES mpa_ratings (id)
    );

CREATE TABLE IF NOT EXISTS director (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(64) NOT NULL
    );

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
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
    film_id  BIGINT REFERENCES films (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT film_genre_pk PRIMARY KEY (film_id, genre_id)
    );
CREATE TABLE IF NOT EXISTS user_friends
(
    user_id    INTEGER REFERENCES users (id) ON DELETE CASCADE,
    friends_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT user_friends_pk PRIMARY KEY (user_id, friends_id)
    );

CREATE TABLE IF NOT EXISTS film_director
(
    film_id  BIGINT REFERENCES films (id) ON DELETE CASCADE,
    director_id BIGINT REFERENCES director (id) ON DELETE CASCADE,
    UNIQUE (director_id, film_id)
    );

CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);
CREATE TABLE IF NOT EXISTS reviews
(
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR,
    is_positive BOOLEAN,
    user_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
    useful INT
);
CREATE TABLE IF NOT EXISTS review_likes
(
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT review_likes_pk PRIMARY KEY (review_id, user_id)
);
CREATE TABLE IF NOT EXISTS review_dislikes
(
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT review_dislikes_pk PRIMARY KEY (review_id, user_id)
);
CREATE TABLE IF NOT EXISTS feed
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_at BIGINT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_type VARCHAR NOT NULL,
    operation VARCHAR NOT NULL,
    entity_id BIGINT NOT NULL
);
INSERT INTO MPA_RATINGS (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
INSERT INTO GENRES (NAME)
VALUES ('??????????????'),
       ('??????????'),
       ('????????????????????'),
       ('??????????????'),
       ('????????????????????????????'),
       ('????????????');