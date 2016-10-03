package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayTypeMapper extends AbstractRegistrableTypeMapper {
    public ArrayTypeMapper() {
        super(ArrayBuilder.class, Array.class, Types.ARRAY);
    }

    @Override
    protected Object toNullSafeJdbcValue(OracleConnection oracleConnection, Object value) throws SQLException {
        return ((ArrayBuilder) value).createArray(oracleConnection);
    }

    @Override
    protected Object toNullSafeJavaValue(OracleConnection connection, Object value) throws SQLException {
        final TypeRegistry typeRegistry = this.getTypeRegistry();
        final Array array = (Array) value;

        return new OracleArrayImpl(typeRegistry, connection, array);
    }
}
