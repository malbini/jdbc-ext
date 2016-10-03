package jdbcext.types;

public interface TypeRegistry {

    boolean containsMapperFor(int sqlType);

    TypeMapper findMapperFor(int sqlType);

    TypeMapper findMapperForJdbcType(Class<?> jdbcType);

    TypeMapper findMapperForJavaType(Class<?> javaType);

    <S,D> boolean containsConverter(Class<S> sourceType, Class<D> destinationType);

    <S,D> TypeConverter<S,D> findConverter(Class<S> sourceType, Class<D> destinationType);

    TypeConverter<Object,Object> findGenericConverter(Class<? extends Object> sourceType, Class<? extends Object> destinationType);
}
