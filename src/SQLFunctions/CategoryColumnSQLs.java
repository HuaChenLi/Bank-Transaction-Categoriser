package src.SQLFunctions;

import java.sql.*;

public class CategoryColumnSQLs extends DatabaseConnection {
    public void createExcelColumnTables() {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS excel_columns " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "audit_id INTEGER NOT NULL, " +
                    "column_name STRING NOT NULL, " +
                    "is_default BOOLEAN, " +
                    "gst_included BOOLEAN, " +
                    "is_income BOOLEAN, " +
                    "sheet_order)";
            stmt.executeUpdate(sql);

            stmt.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Created Excel Column Table successfully");
    }

    public void dropExcelColumnTables() {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();

            String sql = "DROP TABLE IF EXISTS excel_columns";
            stmt.executeUpdate(sql);

            stmt.close();


        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Dropped excel_columns Table successfully");
    }

    public void deleteCategory(int id) {
        try {
            Connection connection = getConnection();
            PreparedStatement deleteCategory = connection.prepareStatement(
                        "DELETE FROM excel_columns " +
                            "WHERE id = ?"
            );
            deleteCategory.setInt(1,id);
            deleteCategory.executeUpdate();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createCategoryMappings() {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS excel_category_mapping " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_id INTEGER NOT NULL, " +
                    "category_values STRING NOT NULL, " +
                    "is_income BOOLEAN)";
            stmt.executeUpdate(sql);

            stmt.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Created Excel Column Table successfully");
    }

    public void createCategory(String s, int accountID, boolean isIncome) {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            PreparedStatement insertColumnSelectionDetails = connection.prepareStatement("""
                    INSERT INTO excel_category_mapping (category_values, account_id, is_income)
                    VALUES (?, ?, ?)
                    """);
            insertColumnSelectionDetails.setString(1, s);
            insertColumnSelectionDetails.setInt(2, accountID);
            insertColumnSelectionDetails.setBoolean(3, isIncome);
            insertColumnSelectionDetails.executeUpdate();
            insertColumnSelectionDetails.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDescription(int id) {
        try {
            Connection connection = getConnection();
            PreparedStatement deleteDescription = connection.prepareStatement(
                        "DELETE FROM excel_category_mapping " +
                            "WHERE id = ? "
            );
            deleteDescription.setInt(1, id);
            deleteDescription.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void insertColumn(int auditID, String columnName, boolean gSTIncluded, boolean isIncome, boolean isExpense) {
        Statement statement;
        if (isIncome) {
            try {
                Connection connection = getConnection();
                PreparedStatement insertColumnDetails = connection.prepareStatement("""
                    INSERT INTO excel_columns (audit_id, column_name, is_default, gst_included, is_income)
                    VALUES (?, ?, ?, ?, ?)
                    """);
                insertColumnDetails.setInt(1, auditID);
                insertColumnDetails.setString(2, columnName);
                insertColumnDetails.setBoolean(3, false);
                insertColumnDetails.setBoolean(4, gSTIncluded);
                insertColumnDetails.setBoolean(5, isIncome);
                insertColumnDetails.executeUpdate();
                insertColumnDetails.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isExpense) {
            try {
                Connection connection = getConnection();
                PreparedStatement insertColumnDetails = connection.prepareStatement("""
                    INSERT INTO excel_columns (audit_id, column_name, is_default, gst_included, is_income)
                    VALUES (?, ?, ?, ?, ?)
                    """);
                insertColumnDetails.setInt(1, auditID);
                insertColumnDetails.setString(2, columnName);
                insertColumnDetails.setBoolean(3, false);
                insertColumnDetails.setBoolean(4, gSTIncluded);
                insertColumnDetails.setBoolean(5, !isExpense);
                insertColumnDetails.executeUpdate();
                insertColumnDetails.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createExcelColumnSelectionTable() {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS excel_column_selection " +
                    "(excel_column_id INTEGER NOT NULL," +
                    "excel_category_mapping_id INTEGER NOT NULL)";
            stmt.executeUpdate(sql);

            stmt.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Created Excel Column Table successfully");
    }

    public void categoriseDescription(int excelColumnID, int excelCategoryMappingID) {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            PreparedStatement insertColumnSelectionDetails = connection.prepareStatement("""
                    INSERT INTO excel_column_selection (excel_column_id, excel_category_mapping_id)
                    VALUES (?, ?)
                    """);
            insertColumnSelectionDetails.setInt(1, excelColumnID);
            insertColumnSelectionDetails.setInt(2, excelCategoryMappingID);
            insertColumnSelectionDetails.executeUpdate();
            insertColumnSelectionDetails.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getExcelColumns(int auditID, boolean isIncome) {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            java.sql.Connection connection = Connection.getConnection();
            PreparedStatement selectExcelColumns = connection.prepareStatement("""
                    SELECT column_name, id, sheet_order FROM excel_columns
                    WHERE audit_id = ?
                    AND is_income = ?
                    ORDER BY sheet_order
                    """);
            selectExcelColumns.setInt(1, auditID);
            selectExcelColumns.setBoolean(2, isIncome);

            ResultSet excelColumns;
            excelColumns = selectExcelColumns.executeQuery();

            return excelColumns;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateColumnName(int id, String name) {
        try {
            Connection connection = getConnection();
            PreparedStatement updateColumnName = connection.prepareStatement(
                    "UPDATE excel_columns " +
                        "SET column_name = ? " +
                        "WHERE id = ?"
            );
            updateColumnName.setString(1, name);
            updateColumnName.setInt(2, id);
            updateColumnName.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateColumnOrder(int id, int order) {
        try {
            Connection connection = getConnection();
            PreparedStatement updateColumnOrder = connection.prepareStatement(
                    "UPDATE excel_columns " +
                        "SET sheet_order = ? " +
                        "WHERE id = ?"
            );
            updateColumnOrder.setInt(1, order);
            updateColumnOrder.setInt(2, id);
            updateColumnOrder.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getDescriptions(int accountID, boolean isIncome) {
        try {
            DatabaseConnection Connection = new DatabaseConnection();
            Connection connection = Connection.getConnection();
            PreparedStatement selectExcelColumns = connection.prepareStatement("""
                    SELECT category_values, id FROM excel_category_mapping
                    WHERE account_id = ?
                    AND is_income = ?
                    """);
            selectExcelColumns.setInt(1, accountID);
            selectExcelColumns.setBoolean(2, isIncome);

            ResultSet excelColumns;
            excelColumns = selectExcelColumns.executeQuery();

            return excelColumns;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLastDescriptionID() {
        try {
            Connection connection = getConnection();
            PreparedStatement selectLastDescription = connection.prepareStatement(
                    "SELECT id FROM excel_category_mapping ORDER BY id DESC LIMIT 1"
            );
            ResultSet rs = selectLastDescription.executeQuery();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            connection.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getDescriptionID(String description, int accountID, boolean isIncome) {
        try {
            Connection connection = getConnection();
            PreparedStatement selectDescriptionID = connection.prepareStatement(
                    "SELECT id FROM excel_category_mapping " +
                            "WHERE account_id = ? " +
                            "AND is_income = ?" +
                            "AND category_values = ? " +
                            "ORDER BY id DESC LIMIT 1 "
            );
            selectDescriptionID.setInt(1, accountID);
            selectDescriptionID.setBoolean(2, isIncome);
            selectDescriptionID.setString(3, description);

            ResultSet rs = selectDescriptionID.executeQuery();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            connection.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isDescriptionAlreadyMapped(String description, int accountID, boolean isIncome) {
        try {
            Connection connection = getConnection();
            PreparedStatement selectDescriptionID = connection.prepareStatement(
                    "SELECT 1 FROM excel_category_mapping " +
                            "INNER JOIN excel_column_selection ON excel_category_mapping.id = excel_column_selection.excel_category_mapping_id " +
                            "INNER JOIN excel_columns ON excel_columns.id = excel_column_selection.excel_column_id " +
                            "WHERE excel_category_mapping.account_id = ? " +
                            "AND excel_category_mapping.is_income = ?" +
                            "AND excel_category_mapping.category_values = ? " +
                            "LIMIT 1 "
            );
            selectDescriptionID.setInt(1, accountID);
            selectDescriptionID.setBoolean(2, isIncome);
            selectDescriptionID.setString(3, description);

            ResultSet rs = selectDescriptionID.executeQuery();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            connection.close();
            return id >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
