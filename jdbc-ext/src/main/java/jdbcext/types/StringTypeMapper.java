package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Types;

public class StringTypeMapper extends AbstractRegistrableTypeMapper {

    public StringTypeMapper() {
        super(String.class, String.class, Types.VARCHAR, Types.CHAR);
    }

    @Override
    protected Object toNullSafeJdbcValue(OracleConnection connection, Object value) throws SQLException {
        return value;
    }

    @Override
    protected Object toNullSafeJavaValue(OracleConnection connection, Object value) throws SQLException {
        return value;
    }
}
