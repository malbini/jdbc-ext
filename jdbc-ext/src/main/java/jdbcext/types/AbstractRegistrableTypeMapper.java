package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;

abstract class AbstractRegistrableTypeMapper implements RegistrableTypeMapper {
    private TypeRegistry typeRegistry;
    private Class<?> javaType;
    private Class<?> jdbcType;
    private int[] sqlTypes;

    protected AbstractRegistrableTypeMapper(Class<?> javaType, Class<?> jdbcType, int ... sqlTypes) {
        this.javaType = javaType;
        this.jdbcType = jdbcType;
        this.sqlTypes = sqlTypes;
    }

    @Override
    public void typeRegistered(TypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    @Override
    public final Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public final Class<?> getJdbcType() {
        return jdbcType;
    }

    @Override
    public final int[] getSqlTypes() {
        return sqlTypes;
    }

    protected TypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    @Override
    public boolean acceptJavaType(Class<?> type) {
        return this.javaType.isAssignableFrom(type);
    }

    @Override
    public final Object toJdbcValue(OracleConnection oracleConnection, Object value) throws SQLException {
        if(value == null) {
            return null;
        }

        final Class<?> javaValueClass = value.getClass();

        if(!this.javaType.isAssignableFrom(javaValueClass)) {
            throw new IllegalArgumentException("Cannot convert [" + javaValueClass.getName() + "] to [" + this.javaType.getName() + "]");
        }

        return this.toNullSafeJdbcValue(oracleConnection, value);
    }

    protected abstract Object toNullSafeJdbcValue(OracleConnection oracleConnection, Object value) throws SQLException;

    @Override
    public Object toJavaValue(OracleConnection connection, Object value) throws SQLException {
        if(value == null) {
            return null;
        }

        final Class<?> jdbcValueClass = value.getClass();

        if(!this.jdbcType.isAssignableFrom(jdbcValueClass)) {
            throw new IllegalArgumentException("Cannot convert [" + jdbcValueClass.getName() + "] to [" + this.jdbcType.getName() + "]");
        }

        return this.toNullSafeJavaValue(connection, value);
    }

    protected abstract Object toNullSafeJavaValue(OracleConnection connection, Object value) throws SQLException;

    protected IllegalArgumentException invalidMappingException(Object value, Class<?> requiredClass) {
        if(requiredClass == null) {
            return new IllegalArgumentException("Cannot map class: [" + value.getClass() + "]");
        }

        return new IllegalArgumentException("Cannot map class: [" + value.getClass() +"] to [" + requiredClass.getClass() + "]");

    }
}
