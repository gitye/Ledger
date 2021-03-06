package ledger.database.storage.SQL.SQLite;

import ledger.database.IDatabase;
import ledger.database.entity.Account;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.exception.StorageException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISQLiteDatabase extends IDatabase {
    Connection getDatabase();

    void rollbackDatabase() throws StorageException;

    void setDatabaseAutoCommit(boolean autoCommit) throws StorageException;

    Account extractAccount(ResultSet rs) throws SQLException;

    Payee lookupAndInsertPayee(Payee currentPayee) throws StorageException;

    Tag lookupAndInsertTag(Tag currentTag) throws StorageException;
}
