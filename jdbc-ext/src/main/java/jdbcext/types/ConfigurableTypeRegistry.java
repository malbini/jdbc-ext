package jdbcext.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurableTypeRegistry implements TypeRegistry {

    private static List<RegistrableTypeMapper> DEFAULT_TYPE_MAPPERS = createDefaultTypeMappers();
    private static List<TypeConverter<?,?>> DEFAULT_TYPE_CONVERTERS = createDefaultTypeConverters();

    private static List<RegistrableTypeMapper> createDefaultTypeMappers() {
        List<RegistrableTypeMapper> defaultTypeMappers = new ArrayList<RegistrableTypeMapper>();

        defaultTypeMappers.add(new StringTypeMapper());
        defaultTypeMappers.add(new NumberTypeMapper());
        defaultTypeMappers.add(new DateTypeMapper());
        defaultTypeMappers.add(new StructTypeMapper());
        defaultTypeMappers.add(new ArrayTypeMapper());

        return Collections.unmodifiableList(defaultTypeMappers);
    }

    private static List<TypeConverter<?,?>> createDefaultTypeConverters() {
        List<TypeConverter<?,?>> defaultTypeConverters = new ArrayList<TypeConverter<?,?>>();

        defaultTypeConverters.add(new TypeConverters.LongToBigDecimalConverter());
        defaultTypeConverters.add(new TypeConverters.IntegerToBigDecimalConverter());
        defaultTypeConverters.add(new TypeConverters.BigDecimalToLongConverter());
        defaultTypeConverters.add(new TypeConverters.BigDecimalToIntegerConverter());

        return Collections.unmodifiableList(defaultTypeConverters);
    }

    private Map<Integer, TypeMapper> sqlTypeMap = new HashMap<Integer, TypeMapper>();
    private Map<Class<?>, TypeMapper> jdbcClassMap = new HashMap<Class<?>, TypeMapper>();
    private Map<Class<?>, TypeMapper> javaClassMap = new HashMap<Class<?>, TypeMapper>();
    private Map<TypeConverterKey, TypeConverter<?,?>> typeConverterMap = new HashMap<TypeConverterKey, TypeConverter<?, ?>>();

    private ConfigurableTypeRegistry(List<RegistrableTypeMapper> typeMappers, List<TypeConverter<?,?>> typeConverters) {
        for(RegistrableTypeMapper typeMapper : typeMappers) {
            this.doRegisterMapper(typeMapper);
        }

        for(TypeConverter<?,?> typeConverter : typeConverters) {
            this.doRegisterConverter(typeConverter);
        }
    }

    public ConfigurableTypeRegistry() {
        this(DEFAULT_TYPE_MAPPERS, DEFAULT_TYPE_CONVERTERS);
    }

    private void doRegisterMapper(RegistrableTypeMapper typeMapper) {
        for(int sqlType : typeMapper.getSqlTypes()) {
            this.sqlTypeMap.put(sqlType, typeMapper);
        }

        this.javaClassMap.put(typeMapper.getJavaType(), typeMapper);
        this.jdbcClassMap.put(typeMapper.getJdbcType(), typeMapper);

        typeMapper.typeRegistered(this);
    }

    private void doRegisterConverter(TypeConverter<?,?> typeConverter) {
        this.typeConverterMap.put(new TypeConverterKey(typeConverter.getSourceType(), typeConverter.getDestinationType()), typeConverter);
    }

    public void registerMapper(RegistrableTypeMapper typeMapper) {
        this.doRegisterMapper(typeMapper);
        typeMapper.typeRegistered(this);
    }

    public void registerConverter(TypeConverter<?,?> typeConverter) {
        this.doRegisterConverter(typeConverter);
    }

    @Override
    public boolean containsMapperFor(int sqlType) {
        return this.sqlTypeMap.containsKey(sqlType);
    }

    @Override
    public TypeMapper findMapperFor(int sqlType) {
        final TypeMapper typeMapper = this.sqlTypeMap.get(sqlType);

        if(typeMapper == null) {
            throw new IllegalArgumentException("TypeMapper not found for sqlType: [" + sqlType + "]");
        }

        return typeMapper;
    }

    @Override
    public TypeMapper findMapperForJdbcType(Class<?> jdbcType) {
        final TypeMapper typeMapper = this.jdbcClassMap.get(jdbcType);

        if(typeMapper == null) {
            for(Class<?> keyClass : this.jdbcClassMap.keySet()){
                if(keyClass.isAssignableFrom(jdbcType)) {
                    return this.jdbcClassMap.get(keyClass);
                }
            }

            throw new IllegalArgumentException("TypeMapper not found for jdbc class: [" + jdbcType.getName() + "]");
        }

        return typeMapper;
    }

    @Override
    public TypeMapper findMapperForJavaType(Class<?> javaType) {
        final TypeMapper typeMapper = this.javaClassMap.get(javaType);

        if(typeMapper == null) {
            for(Class<?> keyClass : this.javaClassMap.keySet()){
                if(keyClass.isAssignableFrom(javaType)) {
                    return this.javaClassMap.get(keyClass);
                }
            }

            throw new IllegalArgumentException("TypeMapper not found for java class: [" + javaType.getName() + "]");
        }

        return typeMapper;
    }

    @Override
    public <S, D> boolean containsConverter(Class<S> sourceType, Class<D> destinationType) {
        final TypeConverterKey typeConverterKey = new TypeConverterKey(sourceType, destinationType);
        return this.typeConverterMap.containsKey(typeConverterKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, D> TypeConverter<S, D> findConverter(Class<S> sourceType, Class<D> destinationType) {
        final TypeConverterKey typeConverterKey = new TypeConverterKey(sourceType, destinationType);
        TypeConverter<S, D> typeConverter = (TypeConverter<S, D>) this.typeConverterMap.get(typeConverterKey);

        if(typeConverter == null) {
            throw new IllegalArgumentException("TypeConverter not found for sourceType: [" + sourceType.getName() + "] and destinationType: [" + destinationType.getName() + "]");
        }

        return typeConverter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypeConverter<Object,Object> findGenericConverter(Class<? extends Object> sourceType, Class<? extends Object> destinationType) {
        return (TypeConverter<Object,Object>) this.findConverter(sourceType, destinationType);
    }

    private static class TypeConverterKey {
        private Class<?> sourceClass;
        private Class<?> destinationClass;

        public TypeConverterKey(Class<?> sourceClass, Class<?> destinationClass) {
            this.sourceClass = sourceClass;
            this.destinationClass = destinationClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeConverterKey that = (TypeConverterKey) o;

            if (!destinationClass.equals(that.destinationClass)) return false;
            if (!sourceClass.equals(that.sourceClass)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sourceClass.hashCode();
            result = 31 * result + destinationClass.hashCode();
            return result;
        }
    }
}
