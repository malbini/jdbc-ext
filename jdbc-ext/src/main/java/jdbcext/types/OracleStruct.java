package jdbcext.types;

import java.math.BigDecimal;
import java.util.Date;

public interface OracleStruct extends StructBuilder, DisposableType {
    OracleStructDescriptor getStructDescriptor();

    void setValue(final String fieldName, final Object value);

    void setStringValue(final String fieldName, String value);

    void setIntegerValue(final String fieldName, Integer value);

    void setLongValue(final String fieldName, Long value);

    void setBigDecimalValue(final String fieldName, BigDecimal value);

    void setDateValue(final String fieldName, Date value);

    Object getValue(final String fieldName);

    <T> T getValue(final String fieldName, Class<T> type);

    String getStringValue(final String fieldName);

    Integer getIntegerValue(final String fieldName);

    Long getLongValue(final String fieldName);

    BigDecimal getBigDecimalValue(final String fieldName);

    Date getDateValue(final String fieldName);
}
