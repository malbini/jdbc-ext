package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;

public interface TypeMapper {

    int[] getSqlTypes();

    Class<?> getJavaType();

    Class<?> getJdbcType();

    boolean acceptJavaType(Class<?> type);

    Object toJdbcValue(OracleConnection oracleConnection, Object value) throws SQLException;

    Object toJavaValue(OracleConnection connection, Object value) throws SQLException;
}
