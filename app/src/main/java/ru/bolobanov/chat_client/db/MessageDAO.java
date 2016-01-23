package ru.bolobanov.chat_client.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.db.mapping.Message;

/**
 * Created by Bolobanov Nikolay on 20.01.16.
 */
public class MessageDAO extends BaseDaoImpl<Message, Integer> {

    MessageDAO(ConnectionSource connectionSource, Class<Message> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Message> getMessages(String pFirst, String pSecond) throws SQLException {
        QueryBuilder<Message, Integer> queryBuilder = this.queryBuilder();
        Where where = queryBuilder.where();
        where.or(
                where.and(
                        where.like(Constants.COLUMN_RECEIVER, pFirst),
                        where.like(Constants.COLUMN_SENDER, pSecond)),
                where.and(
                        where.like(Constants.COLUMN_RECEIVER, pSecond),
                        where.like(Constants.COLUMN_SENDER, pFirst)));
        return queryBuilder.query();
    }

    public void deleteOldMessages(long deadline) throws SQLException {
        DeleteBuilder<Message, Integer> deleteBuilder = deleteBuilder();
        deleteBuilder.where().lt(Constants.COLUMN_TIMESTAMP, deadline);
        deleteBuilder.delete();
    }

}
