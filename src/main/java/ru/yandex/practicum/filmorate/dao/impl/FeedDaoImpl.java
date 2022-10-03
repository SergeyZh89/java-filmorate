package ru.yandex.practicum.filmorate.dao.impl;

import ru.yandex.practicum.filmorate.dao.FeedStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public class FeedDaoImpl implements FeedStorage {
    @Override
    public List<Event> getFeedByUserId(long userId) {//получает ленту событий пользователя
        return null;
    }

    @Override
    public Event addEvent(Event event) {//добавляет событие
        return null;
    }
}
