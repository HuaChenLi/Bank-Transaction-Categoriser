package src.Panels;

import src.Globals;
import src.SQLFunctions.AuditIDSQLs;

import javax.swing.*;
import java.util.Vector;

public class AccountSelectionComboBox extends JComboBox {
    AuditIDSQLs auditIDSQLs = new AuditIDSQLs();
    ExcelColumnViewPanel excelColumnViewPanel;
    MappingPanel mappingPanel;
    public AccountSelectionComboBox() {
        refreshAccountComboBox();
    }

    public void refreshAccountComboBox() {
        this.removeAllItems();
        Vector<AuditAccountID> allAccounts = auditIDSQLs.getAllAccounts();
        for (AuditAccountID a : allAccounts) {
            this.addItem(a);
        }
    }

    public void setPanelsToRefresh(ExcelColumnViewPanel excelColumnViewPanel, MappingPanel mappingPanel) {
        this.excelColumnViewPanel = excelColumnViewPanel;
        this.mappingPanel = mappingPanel;

        this.addActionListener(e -> {
            AuditAccountID auditAccountID = (AuditAccountID) this.getSelectedItem();
            if (auditAccountID == null) {
                AuditAccountClass.setAuditID(auditIDSQLs.getStartingAuditNumber());
                Globals.Account_ID = auditIDSQLs.getStartingAuditNumber();
                Globals.Account_Name = AuditAccountClass.getAccountName();
                AuditAccountClass.setAuditID(Globals.Account_ID);

            } else {
                AuditAccountClass.setAuditID(auditAccountID.id);
            }
            excelColumnViewPanel.refreshAll();
            mappingPanel.refreshMappingTable();
        });

        this.addActionListener(e -> {
            AuditAccountID a = (AuditAccountID) this.getSelectedItem();
            Globals.Account_ID = a.getId();
            Globals.Account_Name = a.getDescription();
        });
    }
}
