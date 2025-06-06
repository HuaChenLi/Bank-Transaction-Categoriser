package src.Panels;

import src.Lib.AlertMessage;
import src.Lib.Logging;
import src.Lib.Transaction;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static src.Globals.Financial_Year;
import static src.Globals.Account_ID;
import static src.Globals.Account_Name;

public class CreateExcelSheetsPanel extends JPanel {
    JButton createExcelSheets;
    JButton createIncomeExpenseCSVs;
    JPanel createCSVPanel;
    ArrayList<File> csvFiles = new ArrayList<>();
    Logging logging = new Logging("create_sheets.log");
    ArrayList<Transaction> transactions = new ArrayList<>();
    public CreateExcelSheetsPanel() {
        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        createExcelSheets = new JButton("Create New Excel Sheets");
        createIncomeExpenseCSVs = new JButton("Create Income and Expense CSVs");

        createExcelSheets.addActionListener(e1 -> {
            try {
                createExcelSheetsFunction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        createIncomeExpenseCSVs.addActionListener(e1 -> {
            if (csvFiles.size() > 0) {
                logging.writeFilesToLog(csvFiles);
                try {
                    createIncomeExpenseCSVsFunction(csvFiles);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No CSVs selected");
            }
        });

        JButton mappingPopupButton = new JButton("Mapping Popup");
        mappingPopupButton.addActionListener(e -> {
            if (csvFiles.size() > 0) {
                mappingPopup();
            } else {
                AlertMessage.errorBox("No CSVs selected", "Alert");
            }
        });

        FileSelector fileSelector = new FileSelector();
        fileSelector.createPanel();

        createCSVPanel = new JPanel();
        createCSVPanel.add(fileSelector);
        createCSVPanel.add(createIncomeExpenseCSVs);

        this.add(createExcelSheets);
        this.add(createCSVPanel);
        this.add(mappingPopupButton);
    }

    public void createExcelSheetsFunction() throws IOException {
        JOptionPane jop = new JOptionPane();
        jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        jop.setMessage("Creating Excel File");
        JDialog dialog = jop.createDialog(null, "Message");

        SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
            boolean isErrorLinesExists;
            @Override
            protected Void doInBackground() throws Exception {
                isErrorLinesExists = false;
                ProcessBuilder pb = new ProcessBuilder("python","Business Audit\\create_folder_structure.py", String.valueOf(Financial_Year), String.valueOf(Account_ID), Account_Name);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String lines = null;
                while ((lines=reader.readLine())!=null) {
                    System.out.println("lines " + lines);
                }

                while ((lines=errorReader.readLine())!=null) {
                    isErrorLinesExists = true;
                    Logging l = new Logging("error.log");
                    l.writeLog("Error lines " + lines);
                    System.out.println("Error lines " + lines);
                }

                return null;
            }
            protected void done() {
                dialog.dispose();
                if (isErrorLinesExists) {
                    AlertMessage.errorBox("Excel File Not Created", "Finished Process");
                } else {
                    AlertMessage.infoBox("Excel File Created", "Finished Process");
                }
            }
        };

        swingWorker.execute();
        dialog.setVisible(true);
    }

    public void createIncomeExpenseCSVsFunction(ArrayList<File> files) throws IOException {
        JOptionPane jop = new JOptionPane();
        jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        jop.setMessage("Creating Income and Expense Sheets");
        JDialog dialog = jop.createDialog(null, "Message");

        SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
            boolean isErrorLinesExists;
            ProcessBuilder pb;
            Process process;
            BufferedReader reader;
            BufferedReader errorReader;

            @Override
            protected Void doInBackground() throws Exception {
                isErrorLinesExists = false;
                try {
                    ArrayList<String> temp = new ArrayList<>();
                    for (File f: files) {
                        temp.add(f.getAbsolutePath());
                    }
                    String arg = String.join("/", temp);

                    pb = new ProcessBuilder("python", "Business Audit\\create_income_expense_csv.py",
                            String.valueOf(AuditAccountClass.getAuditID()),
                            String.valueOf(AuditAccountClass.getAccountName()),
                            String.valueOf(Financial_Year),
                            arg);
                    process = pb.start();
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String lines = null;
                    while ((lines = reader.readLine()) != null) {
                        System.out.println("lines " + lines);
                    }

                    isErrorLinesExists = false;
                    while ((lines = errorReader.readLine()) != null) {
                        Logging l = new Logging("error.log");
                        l.writeLog("Error lines " + lines);
                        System.out.println("Error lines " + lines);
                        isErrorLinesExists = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isErrorLinesExists = true;
                }

                return null;
            }

            // this is called when background thread above has completed.
            protected void done() {
                dialog.dispose();
                if (isErrorLinesExists) {
                    AlertMessage.errorBox("Income and Expense Sheets Not Created", "Finished Process");
                } else {
                    AlertMessage.infoBox("Income and Expense Sheets Created", "Finished Process");
                }
            }
        };

        swingWorker.execute();
        dialog.setVisible(true);
    }

    private void mappingPopup() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

        transactions.clear();

        for (File f : csvFiles) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line = reader.readLine();
                while (line != null) {
                    String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                    line = reader.readLine();

                    Transaction t = new Transaction();
                    t.setDate(LocalDate.parse(splitted[0], formatter));
                    t.setAmount(Double.parseDouble(splitted[1].replace("\"", "")));
                    t.setDescription(splitted[2].replace("\"", ""));

                    transactions.add(t);

                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ClassifyDescriptionsPanel classifyDescriptionsPanel = new ClassifyDescriptionsPanel();
        classifyDescriptionsPanel.addPanels(transactions);

        JFrame newFrame;
        newFrame = new JFrame();
        newFrame.add(classifyDescriptionsPanel, BorderLayout.CENTER);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setTitle("Mapping Organisation");
        newFrame.pack();
        newFrame.setVisible(true);
    }

    public class FileSelector extends JPanel {
        DefaultTableModel dummyTable = new DefaultTableModel() {
            public int getColumnCount() { return 1; }
            public int getRowCount() { return 1;}
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        DefaultTableModel csvTableModel = new DefaultTableModel() {
            public int getColumnCount() { return 1; }
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        JTable csvTable = new JTable(dummyTable);
        JButton openButton = new JButton("Select CSV file");

        public void createPanel() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final JFrame frame = new JFrame("Open File Example");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLayout(new BorderLayout());
                    openButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser chooser = new JFileChooser();
                            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
                            chooser.setFileFilter(filter);
                            chooser.setMultiSelectionEnabled(true);

                            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                                File[] files = chooser.getSelectedFiles();
                                for (File f : files) {
                                    String file = f.getAbsolutePath();
                                    if (!isDuplicateFile(f)) {
                                        csvFiles.add(f);
                                        csvTableModel.addRow(new Object[]{file});
                                        csvTable.setModel(csvTableModel);
                                    } else {
                                        System.out.println("Duplicate File");
                                    }
                                }
                            }
                        }
                    });
                }
            });
            csvTable.getColumnModel().getColumn(0).setPreferredWidth(400);
            csvTable.setAutoCreateColumnsFromModel(false);

            this.add(csvTable);
            this.add(openButton);
        }

        private boolean isDuplicateFile(File file) {
            for (File f : csvFiles) {
                if (file.getAbsolutePath().equals(f.getAbsolutePath())) {
                    return true;
                }
            }
            return false;
        }
    }
}
