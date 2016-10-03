package jdbcext.types;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public final class OracleStructField {
    private String name;
    private int type;
    private String typeName;

    OracleStructField(String name, int type, String typeName) {
        this.name = name;
        this.type = type;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}