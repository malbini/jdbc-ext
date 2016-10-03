package jdbcext.types;

import oracle.sql.StructDescriptor;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OracleStructDescriptor {

    private String name;
    private Map<String, OracleStructField> fields;

    private OracleStructDescriptor(String name, Map<String, OracleStructField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public boolean containsField(String fieldName) {
        return this.fields.containsKey(StringUtils.upperCase(fieldName));
    }

    public OracleStructField getField(String fieldName) {
        return this.fields.get(StringUtils.upperCase(fieldName));
    }

    public Map<String, OracleStructField> getFields() {
        return fields;
    }

    public List<OracleStructField> getOracleStructField() {
        return Collections.unmodifiableList(new ArrayList<OracleStructField>(this.fields.values()));
    }

    public static OracleStructDescriptor createDescriptor(String structName, Connection connection) throws SQLException {
        final StructDescriptor structDescriptor = StructDescriptor.createDescriptor(structName, connection);

        final String name = structDescriptor.getName();
        final Map<String, OracleStructField> fields = readFields(structDescriptor);

        return new OracleStructDescriptor(name, fields);
    }

    private static Map<String, OracleStructField> readFields(StructDescriptor structDescriptor) throws SQLException {
        final ResultSetMetaData rsMetaData = structDescriptor.getMetaData();
        final Map<String, OracleStructField> fields = new LinkedHashMap<String, OracleStructField>(rsMetaData.getColumnCount());

        for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
            final String columnName = rsMetaData.getColumnName(i + 1);
            final int columnType = rsMetaData.getColumnType(i + 1);
            final String columnTypeName = rsMetaData.getColumnTypeName(i + 1);

            fields.put(StringUtils.upperCase(columnName), new OracleStructField(columnName, columnType, columnTypeName));
        }

        return Collections.unmodifiableMap(fields);
    }
}
