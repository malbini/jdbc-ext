package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class OracleArrayImpl implements OracleArray {

    private String typeName;
    private TypeRegistry typeRegistry;
    private List<Object> values = new ArrayList<Object>();

    private transient List<Array> createdArrays = new ArrayList<Array>();

    OracleArrayImpl(String typeName, TypeRegistry typeRegistry) {
        this.typeName = typeName;
        this.typeRegistry = typeRegistry;
    }

    OracleArrayImpl(TypeRegistry typeRegistry, OracleConnection connection, Array array) throws SQLException {
        this.typeName = array.getBaseTypeName();
        this.typeRegistry = typeRegistry;

        Object[] arrayElements = (Object[]) array.getArray();
        TypeMapper typeMapper = null;

        for(int i=0;i<arrayElements.length;i++) {
            final Object arrayValue = arrayElements[i];

            if(arrayValue == null) {
                this.values.add(null);
            } else {
                if(typeMapper == null) {
                    typeMapper = typeRegistry.findMapperForJdbcType(arrayValue.getClass());
                }

                this.values.add(typeMapper.toJavaValue(connection, arrayValue));
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public void add(Object value) {
        this.values.add(value);
    }

    @Override
    public List<Object> getValues() {
        return Collections.unmodifiableList(new ArrayList<Object>(this.values));
    }

    @Override
    public <T> List<T> getValues(Class<T> type) {
        List<T> convertedValues = new ArrayList<T>(this.values.size());

        for(Object value : this.values) {
            convertedValues.add(this.convertValue(value, type));
        }

        return Collections.unmodifiableList(convertedValues);
    }

    @Override
    public Object getValue(int index) {
        return this.values.get(index);
    }

    @Override
    public <T> T getValue(int index, Class<T> type) {
        return this.convertValue(this.values.get(index), type);
    }

    private <T> T convertValue(Object value, Class<T> type) {
        if(value == null) {
            return null;
        }

        if(type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }

        TypeConverter<Object, Object> converter = this.typeRegistry.findGenericConverter(value.getClass(), type);
        return type.cast(converter.convert(value));
    }

    public Array createArray(OracleConnection connection) throws SQLException{
        final Object[] elements = new Object[this.values.size()];

        int index = 0;
        TypeMapper typeMapper = null;

        for(Object value : this.values) {
            Object jdbcValue = null;

            if(value != null) {
                if(typeMapper == null) {
                    typeMapper = this.typeRegistry.findMapperForJavaType(value.getClass());
                }

                jdbcValue = typeMapper.toJdbcValue(connection, value);
            }

            elements[index ++] = jdbcValue;
        }

        final Array array = connection.createOracleArray(this.typeName, elements);
        this.createdArrays.add(array);

        return array;
    }

    @Override
    public void dispose() throws SQLException {
        for(Object value : this.values) {
            if(value instanceof DisposableType) {
                ((DisposableType) value).dispose();
            }
        }

        for(Array array : this.createdArrays) {
            array.free();
        }

        this.createdArrays.clear();
    }
}
