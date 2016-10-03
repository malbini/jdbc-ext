package jdbcext.types;

import java.sql.SQLException;

public interface DisposableType {
    void dispose() throws SQLException;
}
