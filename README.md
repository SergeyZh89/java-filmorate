# java-filmorate
![SQL](/../main/src/main/resources/images/drawSQL2.png)

### Примеры запросов:

Получение списка фильмов:
```
SELECT * FROM film;
```
Получение списка юзеров:
```
SELECT * FROM user;
```
Получение топ популярных N фильмов:
```
SELECT f.name, COUNT (fl.user.id) AS likes
FROM film AS f
JOIN film_likes AS fl ON f.film_id = fl.film_id
GROUP BY f.name
ORDER BY likes DESC
LIMIT N;
```
