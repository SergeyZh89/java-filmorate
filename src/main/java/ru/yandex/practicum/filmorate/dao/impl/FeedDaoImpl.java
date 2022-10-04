package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUserId(long userId) {
        String sqlQuery = "SELECT * " +
                "FROM feed " +
                "WHERE user_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public Event addEvent(Event event) {
        String sqlQuery = "INSERT INTO feed (created_at, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement st = connection.prepareStatement(sqlQuery, new String[]{"id"});
            st.setLong(1, event.getTimestamp());
            st.setLong(2, event.getUserId());
            st.setString(3, event.getEventType());
            st.setString(4, event.getOperation());
            st.setLong(5, event.getEntityId());
            return st;
        }, keyHolder);
        Number key = keyHolder.getKey();
        assert key != null;
        event.setEventId(key.longValue());
        return event;
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(rs.getLong("created_at"),
                rs.getLong("user_id"),
                rs.getString("event_type"),
                rs.getString("operation"),
                rs.getLong("id"),
                rs.getLong("entity_id")
        );
    }
}
