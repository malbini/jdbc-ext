package jdbcext.types;

import java.util.List;

public interface OracleArray extends ArrayBuilder, DisposableType {

    boolean isEmpty();

    int size();

    void add(Object value);

    List<Object> getValues();

    <T> List<T> getValues(Class<T> type);

    Object getValue(int index);

    <T> T getValue(int index, Class<T> type);
}
