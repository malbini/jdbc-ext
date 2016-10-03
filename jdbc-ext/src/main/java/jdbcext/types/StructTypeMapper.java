package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;

public class StructTypeMapper extends AbstractRegistrableTypeMapper {
    public StructTypeMapper() {
        super(StructBuilder.class, Struct.class, Types.STRUCT);
    }

    @Override
    protected Object toNullSafeJdbcValue(OracleConnection connection, Object value) throws SQLException {
        return ((StructBuilder) value).createStruct(connection);
    }

    @Override
    protected Object toNullSafeJavaValue(OracleConnection connection, Object value) throws SQLException {
        final Struct struct = (Struct) value;
        final String typeName = struct.getSQLTypeName();
        final OracleStructDescriptor oracleStructDescriptor = OracleStructDescriptor.createDescriptor(typeName, connection);

        return new OracleStructImpl(oracleStructDescriptor, this.getTypeRegistry(), connection, struct);
    }
}
