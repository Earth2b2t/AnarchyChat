package earth2b2t.anarchychat.h2;

import java.sql.SQLException;

public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException(String message, SQLException cause) {
        super(message, cause);
    }

    public UncheckedSQLException(SQLException cause) {
        super(cause);
    }
}
