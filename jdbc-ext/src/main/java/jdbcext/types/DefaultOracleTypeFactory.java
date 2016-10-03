package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;

public class DefaultOracleTypeFactory implements OracleTypeFactory {

    private static final TypeRegistry DEFAULT_TYPE_REGISTRY = new ConfigurableTypeRegistry();

    private OracleConnection connection;
    private TypeRegistry typeRegistry;

    public DefaultOracleTypeFactory(OracleConnection connection, TypeRegistry typeRegistry) {
        this.connection = connection;
        this.typeRegistry = typeRegistry;
    }

    public DefaultOracleTypeFactory(OracleConnection connection) {
        this(connection, DEFAULT_TYPE_REGISTRY);
    }

    @Override
    public OracleStructImpl createStruct(String typeName) throws SQLException{
        OracleStructDescriptor oracleStructDescriptor = OracleStructDescriptor.createDescriptor(typeName, this.connection);
        return new OracleStructImpl(oracleStructDescriptor, this.typeRegistry);
    }

    @Override
    public OracleArrayImpl createArray(String typeName) throws SQLException {
        return new OracleArrayImpl(typeName, this.typeRegistry);
    }

    @Override
    public OracleStructImpl readStruct(Struct struct) throws SQLException {
        OracleStructDescriptor oracleStructDescriptor = OracleStructDescriptor.createDescriptor(struct.getSQLTypeName(), this.connection);
        return new OracleStructImpl(oracleStructDescriptor, this.typeRegistry, this.connection, struct);
    }

    @Override
    public OracleArrayImpl readArray(Array array) throws SQLException {
        return new OracleArrayImpl(this.typeRegistry, this.connection, array);
    }
}
