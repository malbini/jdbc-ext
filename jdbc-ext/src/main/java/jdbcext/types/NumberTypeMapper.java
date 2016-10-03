package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

public class NumberTypeMapper extends AbstractRegistrableTypeMapper {
    public NumberTypeMapper() {
        super(BigDecimal.class, BigDecimal.class, Types.INTEGER, Types.NUMERIC);
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
