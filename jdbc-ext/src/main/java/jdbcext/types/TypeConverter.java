package jdbcext.types;

public interface TypeConverter<S,D> {

    Class<S> getSourceType();

    Class<D> getDestinationType();

    D convert(S object);
}
