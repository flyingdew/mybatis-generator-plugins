package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.ConnectionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BasePlugin extends PluginAdapter {
    private static Map<String, String> TABLE_MAP = new HashMap<>();

    /**
     * Set properties from the plugin configuration
     * <p>
     * this method will be invoked after the plugin initialized and the context was set,so we can do sth here.
     *
     * @param properties
     * @see ObjectFactory#createPlugin(org.mybatis.generator.config.Context, org.mybatis.generator.config.PluginConfiguration)
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        initTableRemarks();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private synchronized void initTableRemarks() {
        if (TABLE_MAP.size() > 0) {
            return;
        }
        List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
        for (TableConfiguration configuration : tableConfigurations) {
            String tableName = configuration.getTableName();
            TABLE_MAP.put(tableName, getTableRemarks(configuration));
        }
    }

    private String getTableRemarks(TableConfiguration tableConfiguration) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
        Connection connection = null;
        try {
            connection = getConnection(jdbcConnectionConfiguration);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(tableConfiguration.getCatalog(), tableConfiguration.getSchema(), tableConfiguration.getTableName(), null);
            rs.next();
            return rs.getString("REMARKS");
        } catch (SQLException ignored) {
        } finally {
            closeConnection(connection);
        }
        return null;
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
    private Connection getConnection(JDBCConnectionConfiguration jdbcConnectionConfiguration) throws SQLException {
        return ConnectionFactory.getInstance().getConnection(jdbcConnectionConfiguration);
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    String getTableRemarks(String tableName) {
        return TABLE_MAP.get(tableName);
    }
}
