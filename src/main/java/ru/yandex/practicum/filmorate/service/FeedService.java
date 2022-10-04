package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FeedDaoImpl;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedDaoImpl feedDao;

    public List<Event> getFeedByUserId(Long userId) {
        return feedDao.getFeedByUserId(userId);
    }

    public Event addEvent(Event event) {
        return feedDao.addEvent(event);
    }
}
