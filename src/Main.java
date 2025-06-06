package src;

import src.Lib.Logging;
import src.Panels.*;
import src.SQLFunctions.AuditIDSQLs;
import src.SQLFunctions.CategoryColumnSQLs;
import src.SQLFunctions.MappingTableSQLs;

import javax.swing.*;
import java.awt.*;

public class Main {
    JFrame frame;
    JPanel mainPanel;
    Logging logging = new Logging("audit_codes.log");

    public Main() {
        logging.writeLog("=================================================");
        logging.writeLog("starting audit_codes");
//        Setting the UI to look more like Windows 11
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

//        Load Database
        AuditIDSQLs auditIDSQLs = new AuditIDSQLs();
        auditIDSQLs.createAuditTable();
        AuditAccountClass.setAuditID(auditIDSQLs.getStartingAuditNumber());

//        Account Creation Panel
        CreateAccountPanel createAccountPanel = new CreateAccountPanel();

        CategoryColumnSQLs categoryColumnSQLs = new CategoryColumnSQLs();
        categoryColumnSQLs.createExcelColumnTables();
        categoryColumnSQLs.createExcelColumnSelectionTable();
        categoryColumnSQLs.createCategoryMappings();

        MappingTableSQLs mappingTableSQLs = new MappingTableSQLs();
        mappingTableSQLs.createMappingTable();
        mappingTableSQLs.createMappingSelectionTable();

        ExcelColumnViewPanel excelColumnViewPanel = new ExcelColumnViewPanel();

        mainPanel.add(createAccountPanel);

//        Title Panel
        TitlePanel titlePanel = new TitlePanel();
        mainPanel.add(titlePanel);


//        Financial Year Panel
        FinancialYearPanel financialYearPanel = new FinancialYearPanel();
        mainPanel.add(financialYearPanel);

//        Create Excel Spreadsheets Panel
        CreateExcelSheetsPanel createExcelSpreadsheetsPanel = new CreateExcelSheetsPanel();
        mainPanel.add(createExcelSpreadsheetsPanel);

//        Income Expense Indicator Panel
        IncomeExpenseIndicatorPanel incomeExpenseIndicatorPanel = new IncomeExpenseIndicatorPanel();
        mainPanel.add(incomeExpenseIndicatorPanel);

//        Mapping Panel
        MappingPanel mappingPanel = new MappingPanel();
        mainPanel.add(mappingPanel);
        createAccountPanel.setPanelsToRefresh(excelColumnViewPanel, mappingPanel);

//        Column Creation Panel
        CreateCategoryPanel columnCreationPanel = new CreateCategoryPanel(excelColumnViewPanel);
        mainPanel.add(columnCreationPanel);

//        Category Creation Panel
        KnownDescriptionPanel knownDescriptionPanel = new KnownDescriptionPanel(excelColumnViewPanel);
        mainPanel.add(knownDescriptionPanel);
        mappingPanel.setKnownDescriptionPanel(knownDescriptionPanel);

//        Categorise Panel
        CategoriseValuesPanel categoriseValuesPanel = new CategoriseValuesPanel();
        excelColumnViewPanel.setCategoriseValuesPanel(categoriseValuesPanel);
        knownDescriptionPanel.setCategoriseValuesPanel(categoriseValuesPanel);
        mainPanel.add(categoriseValuesPanel);

        mainPanel.add(excelColumnViewPanel);

        Gui.setMappingPanel(mappingPanel);
        Gui.setExcelColumnViewPanel(excelColumnViewPanel);

//        Setting the GUI Frame
        frame = new JFrame();
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Bank Account Organisation");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}