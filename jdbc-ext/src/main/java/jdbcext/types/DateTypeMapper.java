package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

public class DateTypeMapper extends AbstractRegistrableTypeMapper {

    public DateTypeMapper() {
        super(Date.class, Timestamp.class, Types.DATE);
    }

    @Override
    protected Object toNullSafeJdbcValue(OracleConnection connection, Object value) throws SQLException {
        if(value instanceof Timestamp) {
            return value;
        }

        return new Timestamp(((Date) value).getTime());
    }

    @Override
    protected Object toNullSafeJavaValue(OracleConnection connection, Object value) throws SQLException {
        if(value instanceof Timestamp) {
            return value;
        }

        if(value instanceof java.sql.Date) {
            return new Date(((java.sql.Date) value).getTime());
        }

        throw this.invalidMappingException(value, Timestamp.class);
    }
}
