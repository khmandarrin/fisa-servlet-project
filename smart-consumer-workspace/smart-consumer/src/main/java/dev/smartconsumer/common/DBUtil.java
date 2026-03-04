package dev.smartconsumer.common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * DB 커넥션 유틸리티.
 * Spring 빈으로 등록되며, 두 개의 DataSource를 주입받아 제공.
 * 
 * 참고: DAO에서 직접 DataSource를 주입받으므로
 * 이 클래스는 필요 시 사용할 수 있는 유틸리티 빈입니다.
 */
@Component
public class DBUtil {
    
    // 읽기 전용인지 쓰기 전용인지 구분하기 위한 Enum
    public enum DBType { SOURCE, REPLICA }

    private final DataSource sourceDs;
    private final DataSource replicaDs;

    public DBUtil(@Qualifier("sourceDataSource") DataSource sourceDs,
                  @Qualifier("replicaDataSource") DataSource replicaDs) {
        this.sourceDs = sourceDs;
        this.replicaDs = replicaDs;
    }

    public Connection getConnection(DBType type) throws SQLException {
        DataSource ds = (type == DBType.SOURCE) ? sourceDs : replicaDs;
        return ds.getConnection();
    }
}