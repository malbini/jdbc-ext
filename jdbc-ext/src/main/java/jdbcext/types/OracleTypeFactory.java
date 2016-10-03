package jdbcext.types;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;

public interface OracleTypeFactory {
    OracleStruct createStruct(String typeName) throws SQLException;

    OracleArray createArray(String typeName) throws SQLException;

    OracleStruct readStruct(Struct struct) throws SQLException;

    OracleArray readArray(Array array) throws SQLException;
}
