package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.ObjectFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BasePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * get the db connection
     * <p>
     * we can see it in Context#getConnection()
     *
     * @param jdbcConnectionConfiguration
     * @return
     * @throws SQLException
     */
    protected Connection getConnection(JDBCConnectionConfiguration jdbcConnectionConfiguration) throws SQLException {
        ConnectionFactory connectionFactory;
        if (jdbcConnectionConfiguration != null) {
            connectionFactory = new JDBCConnectionFactory(jdbcConnectionConfiguration);
        } else {
            connectionFactory = ObjectFactory.createConnectionFactory(context);
        }
        return connectionFactory.getConnection();
    }

    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
