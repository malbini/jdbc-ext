package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Struct;

public interface StructBuilder {
    Struct createStruct(OracleConnection connection) throws SQLException;
}
