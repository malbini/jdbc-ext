package jdbcext.types;

import oracle.jdbc.OracleConnection;

import java.sql.Array;
import java.sql.SQLException;

public interface ArrayBuilder {
    Array createArray(OracleConnection connection) throws SQLException;
}
