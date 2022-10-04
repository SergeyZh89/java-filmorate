package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedDao {

    List<Event> getFeedByUserId(long userId);

    Event addEvent(Event event);
}
