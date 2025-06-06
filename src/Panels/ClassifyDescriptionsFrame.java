package src.Panels;

import src.Gui;
import src.Lib.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ClassifyDescriptionsFrame extends JFrame {
    ArrayList<Transaction> transactions = new ArrayList<>();
    public ClassifyDescriptionsFrame(ArrayList<File> csvFiles) {

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

        ClassifyDescriptionsPanel classifyDescriptionsPanel = new ClassifyDescriptionsPanel(this, transactions);

        this.add(classifyDescriptionsPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // call terminate
                Gui.refresh();
            }
        });

        this.setTitle("Mapping Organisation");
        this.pack();
        this.setVisible(true);

    }

}
