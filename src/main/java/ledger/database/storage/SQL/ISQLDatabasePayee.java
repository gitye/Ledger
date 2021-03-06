package ledger.database.storage.SQL;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.PayeeTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLDatabasePayee extends ISQLiteDatabase {

    @Override
    default void insertPayee(Payee payee) throws StorageException {
        try {

            PreparedStatement checkIfExistsStmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_ID + ", "
                    + PayeeTable.PAYEE_NAME + ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME + " WHERE " + PayeeTable.PAYEE_NAME + "=?");
            checkIfExistsStmt.setString(1, payee.getName());

            ResultSet existingPayees = checkIfExistsStmt.executeQuery();
            if (existingPayees.next()) {
                int payeeID = existingPayees.getInt(PayeeTable.PAYEE_ID);
                String payeeName = existingPayees.getString(PayeeTable.PAYEE_NAME);
                String payeeDescription = existingPayees.getString(PayeeTable.PAYEE_DESC);

                payee.setId(payeeID);
                payee.setName(payeeName);
                payee.setDescription(payeeDescription);
                payee.setTags(getAllTagsForPayee(payee));
                return;
            }

            PreparedStatement stmt =
                    getDatabase().prepareStatement("INSERT INTO " + PayeeTable.TABLE_NAME +
                            " (" + PayeeTable.PAYEE_NAME +
                            ", " + PayeeTable.PAYEE_DESC +
                            ") VALUES (?, ?)");
            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedPayeeID = generatedIDs.getInt(1);
                payee.setId(insertedPayeeID);
            }

            if (payee.getTags() != null) {
                for (Tag t : payee.getTags()) {
                    lookupAndInsertTag(t);
                    addTagForPayee(t, payee);
                }
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }

    @Override
    default void deletePayee(Payee payee) throws StorageException {
        try {
            deleteAllTagsForPayee(payee);
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_ID + " = ?");
            stmt.setInt(1, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Payee", e);
        }
    }

    @Override
    default void editPayee(Payee payee) throws StorageException {
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = getDatabase().getAutoCommit();
            setDatabaseAutoCommit(false);

            PreparedStatement stmt =
                    getDatabase().prepareStatement("UPDATE " + PayeeTable.TABLE_NAME +
                            " SET " + PayeeTable.PAYEE_NAME +
                            " = ?, " + PayeeTable.PAYEE_DESC +
                            " = ? WHERE " + PayeeTable.PAYEE_ID + " = ?");

            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.setInt(3, payee.getId());

            if (payee.getTags() != null) {
                deleteAllTagsForPayee(payee);
                for (Tag t : payee.getTags()) {
                    lookupAndInsertTag(t);
                    addTagForPayee(t, payee);
                }
            }

            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Payee", e);
        } finally {
            setDatabaseAutoCommit(originalAutoCommit);
        }
    }

    @Override
    default List<Payee> getAllPayees() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + PayeeTable.PAYEE_ID +
                    ", " + PayeeTable.PAYEE_NAME +
                    ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME + ";");

            ArrayList<Payee> payeeList = new ArrayList<>();

            while (rs.next()) {
                payeeList.add(extractPayee(rs));
            }
            return payeeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

    @Override
    default Payee getPayeeById(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_ID +
                    ", " + PayeeTable.PAYEE_NAME +
                    ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_ID + "=?");

            stmt.setInt(1, payee.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPayee(rs);
            }
            return null;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

    default Payee extractPayee(ResultSet rs) throws SQLException, StorageException {
        int payeeID = rs.getInt(PayeeTable.PAYEE_ID);
        String payeeName = rs.getString(PayeeTable.PAYEE_NAME);
        String payeeDesc = rs.getString(PayeeTable.PAYEE_DESC);
        List<Tag> tags = getAllTagsForPayeeId(payeeID);

        return new Payee(payeeName, payeeDesc, payeeID, tags);
    }

    @Override
    default List<Tag> getAllTagsForPayee(Payee payee) throws StorageException {
        int id = payee.getId();
        if (id == -1) {
            insertPayee(payee);
        }
        return getAllTagsForPayeeId(payee.getId());
    }

    @Override
    default Payee lookupAndInsertPayee(Payee currentPayee) throws StorageException {
        Payee existingPayee;
        if (currentPayee.getId() != -1) {
            existingPayee = currentPayee;
        } else {
            existingPayee = getPayeeForNameAndDescription(currentPayee.getName(), currentPayee.getDescription());
        }
        if (existingPayee != null) {
            currentPayee.setId(existingPayee.getId());
        } else {
            insertPayee(currentPayee);
            currentPayee = getPayeeForNameAndDescription(currentPayee.getName(), currentPayee.getDescription());
        }
        return currentPayee;
    }

}
