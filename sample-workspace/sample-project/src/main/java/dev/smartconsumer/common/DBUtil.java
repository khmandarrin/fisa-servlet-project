package dev.smartconsumer.common;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    
    // 읽기 전용인지 쓰기 전용인지 구분하기 위한 Enum
    public enum DBType { SOURCE, REPLICA }

    public static Connection getConnection(ServletContext ctx, DBType type) throws SQLException {
        DataSource ds;
        if (type == DBType.SOURCE) {
            ds = (DataSource) ctx.getAttribute("DS_SOURCE");
        } else {
            ds = (DataSource) ctx.getAttribute("DS_REPLICA");
        }
        
        if (ds == null) {
            throw new SQLException("DataSource not found in ServletContext for type: " + type);
        }
        return ds.getConnection();
    }
}