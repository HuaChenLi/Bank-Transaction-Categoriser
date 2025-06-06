package src.Lib;

import src.SQLFunctions.CategoryColumnSQLs;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

import static src.SQLFunctions.DatabaseConnection.buildTableModel;

public class DescData {
    private static final CategoryColumnSQLs categoryColumnSQLs = new CategoryColumnSQLs();
    public static DefaultTableModel getDescriptionDataModel(int accountID, boolean isIncome) throws SQLException {
        ResultSet excelColumns;
        excelColumns = categoryColumnSQLs.getDescriptions(accountID, isIncome);

        return buildTableModel(excelColumns);
    }
}
