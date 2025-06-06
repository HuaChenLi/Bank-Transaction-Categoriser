package src.Panels;

import src.Lib.AlertMessage;
import src.Lib.GhostText;
import src.Lib.Transaction;
import src.SQLFunctions.CategoryColumnSQLs;
import src.SQLFunctions.MappingTableSQLs;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassifyDescriptionsPanel extends JPanel {
    JScrollPane scrollPane;
    JPanel innerPanel;
    MappingTableSQLs mappingTableSQLs = new MappingTableSQLs();
    Border border = BorderFactory.createLineBorder(Color.decode("#3037ff"));
    ArrayList<Transaction> transactions;
    public void addPanels(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(innerPanel);
        scrollPane.setPreferredSize(new Dimension(2000,1000));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(BorderLayout.CENTER, scrollPane);

        Collections.sort(transactions);

        ArrayList<String> incomeMapFromValues;
        ArrayList<String> expenseMapFromValues;
        incomeMapFromValues = mappingTableSQLs.getMapFrom(AuditAccountClass.getAuditID(), true);
        expenseMapFromValues = mappingTableSQLs.getMapFrom(AuditAccountClass.getAuditID(), false);

        for (Transaction t: transactions) {
            boolean isMatched = false;
            if (t.isIncome()){
                for (String s: incomeMapFromValues) {
                    Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(t.getDescription());
                    if (matcher.find()) {
                        isMatched = true;
                        break;
                    }
                }
            } else {
                for (String s: expenseMapFromValues) {
                    Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(t.getDescription());
                    if (matcher.find()) {
                        isMatched = true;
                        break;
                    }
                }
            }
            if (!isMatched) {
                addPanel(t);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getViewport().setViewPosition( new Point(0, 0) );
            }
        });
    }

    public void addPanel(Transaction t) {
        JLabel dateLabel = new JLabel(t.getDate().toString());
        JTextPane descLabel = new JTextPane();
        Font defaultFont = new JLabel().getFont();
        descLabel.setFont(defaultFont);

        descLabel.setText(t.getDescription());
        descLabel.setEditable(false);
        descLabel.setBackground(null);
        descLabel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        descLabel.setMaximumSize(new Dimension(500, 30));

        JLabel amtLabel = new JLabel(String.valueOf(t.getAmount()));
        JTextField mapFrom = new JTextField();
        JTextField mapTo = new JTextField();
        JButton createMapping = new JButton("Create Mapping");
        JButton findExistingMapping = new JButton("Find Existing Mapping");
        JButton categoriseWithNoMapping = new JButton("Categorise with no mapping");

        GhostText ghostText0 = new GhostText(mapFrom, "Please Enter Map From Value");
        GhostText ghostText1 = new GhostText(mapTo, "Please Enter Map To Value");

        createMapping.addActionListener(new CreateMappingActionListener(t, mapFrom, mapTo));

        findExistingMapping.addActionListener(e -> {
            FindExistingMappingPanel panel = new FindExistingMappingPanel(t.isIncome());
            String title = t.isIncome() ? "Income Descriptions" : "Expense Descriptions";
            int selection = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, new String[]{"OK", "Cancel"},"OK");

            if ((selection == -1 || selection == 0) && panel.getSelectedDescription() != null) {
                mapTo.setText(panel.getSelectedDescription());
            }
        });

        categoriseWithNoMapping.addActionListener(e -> {
            System.out.println(t.getDescription());
            char incomeExpenseChar = t.isIncome() ? 'I' : 'E';
            mappingTableSQLs.insertMapping(t.getDescription(), t.getDescription(), AuditAccountClass.getAuditID(), incomeExpenseChar);
            refresh();
            handleCategories(t.getDescription(), t.getDescription(), t);
        });

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();

        p1.setMinimumSize(new Dimension(65,10));
        p1.setPreferredSize(new Dimension(65,10));
        p1.add(dateLabel);
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setAlignmentX(Component.LEFT_ALIGNMENT);

        p2.setMinimumSize(new Dimension(500,10));
        p2.setPreferredSize(new Dimension(500,10));
        p2.add(descLabel);
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setAlignmentX(Component.LEFT_ALIGNMENT);

        p3.setMinimumSize(new Dimension(65,10));
        p3.setPreferredSize(new Dimension(65,10));
        p3.add(amtLabel);
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(border);

        panel.add(p1);
        panel.add(p2);
        panel.add(p3);
        panel.add(mapFrom);
        panel.add(mapTo);
        panel.add(createMapping);
        panel.add(findExistingMapping);
        panel.add(categoriseWithNoMapping);

        innerPanel.add(panel);
        innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public void refresh() {
        innerPanel.removeAll();
        innerPanel.repaint();
        innerPanel.revalidate();

        ArrayList<String> incomeMapFromValues;
        ArrayList<String> expenseMapFromValues;
        incomeMapFromValues = mappingTableSQLs.getMapFrom(AuditAccountClass.getAuditID(), true);
        expenseMapFromValues = mappingTableSQLs.getMapFrom(AuditAccountClass.getAuditID(), false);

        for (Transaction t: transactions) {
            boolean isMatched = false;
            if (t.isIncome()){
                for (String s: incomeMapFromValues) {
                    Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(t.getDescription());
                    if (matcher.find()) {
                        isMatched = true;
                        break;
                    }
                }
            } else {
                for (String s: expenseMapFromValues) {
                    Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(t.getDescription());
                    if (matcher.find()) {
                        isMatched = true;
                        break;
                    }
                }
            }
            if (!isMatched) {
                addPanel(t);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getViewport().setViewPosition( new Point(0, 0) );
            }
        });
    }

    public class CreateMappingActionListener implements ActionListener {
        Transaction t;
        JTextField mapFrom;
        JTextField mapTo;
        public CreateMappingActionListener(Transaction t, JTextField mapFrom, JTextField mapTo) {
            this.t = t;
            this.mapFrom = mapFrom;
            this.mapTo = mapTo;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mapTo.getText().trim().length() == 0 || mapFrom.getText().trim().length() == 0) {
                AlertMessage.errorBox("No Value in Text Field", "Alert");
                return;
            }

            Pattern pattern = Pattern.compile(mapFrom.getText().trim(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(t.getDescription());
            if (!matcher.find()) {
                AlertMessage.errorBox("Raw data doesn't get mapped", "Alert");
                return;
            }

            String mapFromValue = mapFrom.getText().trim();
            String mapToValue = mapTo.getText().trim();
            char incomeExpenseChar = t.isIncome() ? 'I' : 'E';
            mappingTableSQLs.insertMapping(mapFromValue, mapToValue, AuditAccountClass.getAuditID(), incomeExpenseChar);
            mapFrom.setText("");
            mapTo.setText("");
            refresh();

            handleCategories(mapFromValue, mapToValue, t);
        }
    }

    private void handleCategories(String mapFromValue, String mapToValue, Transaction t) {
        CategoryColumnSQLs categoryColumnSQLs = new CategoryColumnSQLs();
        if (!categoryColumnSQLs.isDescriptionAlreadyMapped(mapToValue, AuditAccountClass.getAuditID(), t.isIncome())) {

            int reply = JOptionPane.showConfirmDialog(null, "Would you like to categorise Description", null, JOptionPane.YES_NO_OPTION);

            if (reply == JOptionPane.YES_NO_OPTION) {
                FindExistingCategoryPanel panel = new FindExistingCategoryPanel(t,  mapToValue);
                String title = t.isIncome() ? "Income Categories" : "Expense Categories";
                int selection = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new String[]{"OK", "Cancel"},"OK");

                if ((selection == -1 || selection == 0) && panel.getSelectedCategory() != null) {
//                    This parts a bit messy and repeats itself
                    int descriptionID = categoryColumnSQLs.getDescriptionID(mapFromValue, AuditAccountClass.getAuditID(), t.isIncome());
                    if (descriptionID < 0) {
                        categoryColumnSQLs.createCategory(mapToValue, AuditAccountClass.getAuditID(), t.isIncome());
                        descriptionID = categoryColumnSQLs.getDescriptionID(mapToValue, AuditAccountClass.getAuditID(), t.isIncome());
                    }

                    if (descriptionID >= 0) {
                        categoryColumnSQLs.categoriseDescription(panel.getCategoryID(), descriptionID);
                    } else {
//                            Shouldn't hit here, but you never know
                        AlertMessage.errorBox("Could not categorise description", "Warning");
                    }
                }
            }

            AlertMessage.infoBox("Mapping Created", "Mapping Information");
        }
    }
}
