package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class OracleStructImpl implements OracleStruct {

    private OracleStructDescriptor oracleStructDescriptor;
    private TypeRegistry typeRegistry;
    private Map<String, Object> valueMap = new LinkedHashMap<String, Object>();

    private transient List<Struct> createdStruct = new ArrayList<Struct>();

    OracleStructImpl(OracleStructDescriptor oracleStructDescriptor, TypeRegistry typeRegistry) {
        this.oracleStructDescriptor = oracleStructDescriptor;
        this.typeRegistry = typeRegistry;
    }

    OracleStructImpl(OracleStructDescriptor oracleStructDescriptor, TypeRegistry typeRegistry, OracleConnection connection, Struct struct) throws SQLException {
        this(oracleStructDescriptor, typeRegistry);
        this.read(connection, struct);
    }

    private void read(OracleConnection connection, Struct struct) throws SQLException {
        final Object[] attributes = struct.getAttributes();

        final Map<String, OracleStructField> fields = this.oracleStructDescriptor.getFields();
        final String[] fieldNames = fields.keySet().toArray(new String[fields.size()]);

        for(int i=0;i<attributes.length;i++){
            final String fieldName = fieldNames[i];
            final OracleStructField field = fields.get(fieldName);
            final TypeMapper typeMapper = this.typeRegistry.findMapperFor(field.getType());
            final Object value = typeMapper.toJavaValue(connection, attributes[i]);
            this.valueMap.put(fieldName, value);
        }
    }

    @Override
    public OracleStructDescriptor getStructDescriptor() {
        return this.oracleStructDescriptor;
    }

    @Override
    public void setValue(final String fieldName, final Object value) {
        if(!this.oracleStructDescriptor.containsField(fieldName)) {
            throw new IllegalArgumentException("Field not found: [" + fieldName + "]");
        }

        final OracleStructField oracleStructField = this.oracleStructDescriptor.getField(fieldName);

        if(!this.typeRegistry.containsMapperFor(oracleStructField.getType())) {
            throw new IllegalArgumentException("Invalid value for field: [" + fieldName + "]");
        }

        if(value != null) {
            // Verifica tipologia campo impostato

            final Class<?> valueClass = value.getClass();
            final TypeMapper typeMapper = this.typeRegistry.findMapperFor(oracleStructField.getType());

            if(!typeMapper.acceptJavaType(valueClass) && !this.typeRegistry.containsConverter(valueClass, typeMapper.getJavaType())) {
                throw new IllegalArgumentException("Invalid value type: [" + valueClass.getName() + "] for field: [" + fieldName + "]");
            }
        }

        this.valueMap.put(fieldName, value);
    }

    @Override
    public void setStringValue(String fieldName, String value) {
        this.setValue(fieldName, value);
    }

    @Override
    public void setIntegerValue(String fieldName, Integer value) {
        this.setValue(fieldName, value);
    }

    @Override
    public void setLongValue(String fieldName, Long value) {
        this.setValue(fieldName, value);
    }

    @Override
    public void setBigDecimalValue(String fieldName, BigDecimal value) {
        this.setValue(fieldName, value);
    }

    @Override
    public void setDateValue(String fieldName, Date value) {
        this.setValue(fieldName, value);
    }

    @Override
    public Object getValue(final String fieldName) {
        if(!this.oracleStructDescriptor.containsField(fieldName)) {
            throw new IllegalArgumentException("Field not found: [" + fieldName + "]");
        }

        return this.valueMap.get(fieldName);
    }

    @Override
    public <T> T getValue(final String fieldName, Class<T> type) {
        final Object value = this.getValue(fieldName);

        if(value == null) {
            return null;
        }

        if(type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }

        TypeConverter<Object, Object> converter = this.typeRegistry.findGenericConverter(value.getClass(), type);
        return type.cast(converter.convert(value));
    }

    @Override
    public String getStringValue(String fieldName) {
        return this.getValue(fieldName, String.class);
    }

    @Override
    public Integer getIntegerValue(String fieldName) {
        return this.getValue(fieldName, Integer.class);
    }

    @Override
    public Long getLongValue(String fieldName) {
        return this.getValue(fieldName, Long.class);
    }

    @Override
    public BigDecimal getBigDecimalValue(String fieldName) {
        return this.getValue(fieldName, BigDecimal.class);
    }

    @Override
    public Date getDateValue(String fieldName) {
        return this.getValue(fieldName, Date.class);
    }

    @Override
    public Struct createStruct(OracleConnection connection) throws SQLException {
        final Map<String, OracleStructField> fields = this.oracleStructDescriptor.getFields();
        final Object[] attributes = new Object[fields.size()];

        int index = 0;

        for(Map.Entry<String,OracleStructField> entry : fields.entrySet()) {
            final String fieldName = entry.getKey();
            final OracleStructField oracleStructField = entry.getValue();

            Object jdbcValue = null;

            if(this.valueMap.containsKey(fieldName)) {
                Object value = this.valueMap.get(fieldName);

                Class<?> valueClass = null;

                if(value != null) {
                    valueClass = value.getClass();
                }

                final TypeMapper typeMapper = this.typeRegistry.findMapperFor(oracleStructField.getType());

                if(!typeMapper.acceptJavaType(valueClass)) {
                    final TypeConverter<Object,Object> typeConverter = this.typeRegistry.findGenericConverter(valueClass, typeMapper.getJavaType());
                    jdbcValue = typeConverter.convert(value);
                } else {
                    jdbcValue = typeMapper.toJdbcValue(connection, value);
                }
            }

            attributes[index ++] = jdbcValue;
        }

        final Struct struct = connection.createStruct(this.oracleStructDescriptor.getName(), attributes);
        this.createdStruct.add(struct);

        return struct;
    }

    @Override
    public void dispose() throws SQLException {
        for(Object value : this.valueMap.values()) {
            if(value instanceof DisposableType) {
                ((DisposableType) value).dispose();
            }
        }

        this.createdStruct.clear();
    }

    @Override
    public String toString() {
        return this.valueMap.toString();
    }
}
