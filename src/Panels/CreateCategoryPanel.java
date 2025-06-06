package src.Panels;

import src.SQLFunctions.CategoryColumnSQLs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CreateCategoryPanel extends JPanel {
    JLabel columnNameLabel, defaultLabel, gSTLabel, blankLabel;
    JTextField columnNameText;
    JButton defaultButton, gSTButton, addColumnButton;
    boolean isDefault = false, gSTIncluded = false;
    ExcelColumnViewPanel excelColumnViewPanel;
    public CreateCategoryPanel(ExcelColumnViewPanel excelColumnViewPanel) {
        this.setLayout(new GridLayout(0,4));
        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        columnNameLabel = new JLabel("Column Name");
        defaultLabel = new JLabel("Default?");
        gSTLabel = new JLabel("GST Included?");
        gSTButton = new JButton("GST Not Included");
        addColumnButton = new JButton("Create Category");
        columnNameText = new JTextField();
        columnNameText.addKeyListener(new CreateColumnKeyListener());
        blankLabel = new JLabel();
        defaultButton = new JButton("Not Default");
        defaultButton.addActionListener(e1 -> {
            isDefault = !isDefault;
            if (isDefault) {
                defaultButton.setText("Is Default");
            } else {
                defaultButton.setText("Not Default");
            }
        });
        gSTButton.addActionListener(e1 -> {
            gSTIncluded = !gSTIncluded;
            if (gSTIncluded) {
                gSTButton.setText("GST Included");
            } else {
                gSTButton.setText("GST Not Included");
            }
        });

        this.excelColumnViewPanel = excelColumnViewPanel;

        addColumnButton.addActionListener(e1 -> createExcelColumn());


        this.add(columnNameLabel);
        this.add(gSTLabel);
        this.add(defaultLabel);
        this.add(blankLabel);
        this.add(columnNameText);
        this.add(addColumnButton);
        this.add(gSTButton);
        this.add(defaultButton);
        this.add(addColumnButton);
    }

    private void createExcelColumn() {
        CategoryColumnSQLs categoryColumnSQLs = new CategoryColumnSQLs();
        if (columnNameText.getText().trim().length() >= 1) {
            categoryColumnSQLs.createCategory(AuditAccountClass.getAuditID(), columnNameText.getText(), gSTIncluded, AuditAccountClass.isIncome(), AuditAccountClass.isExpense());
        }
        columnNameText.setText("");

        excelColumnViewPanel.refreshAll();
    }

    private class CreateColumnKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                createExcelColumn();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
