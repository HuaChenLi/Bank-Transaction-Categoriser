package src.Panels;

import src.Globals;

import javax.swing.*;
import java.time.Year;

public class FinancialYearPanel extends JPanel {
    JLabel financialYearLabel;
    YearComboBox yearComboBox;
    public FinancialYearPanel() {
        financialYearLabel = new JLabel("Year");
        yearComboBox = new YearComboBox();
        int year = Year.now().getValue();

        yearComboBox.setSelectedItem(year);
        Globals.Financial_Year = year;

        this.add(financialYearLabel);
        this.add(yearComboBox);
    }

    public class YearComboBox extends JComboBox {
        public YearComboBox() {
            for (int i = 0; i < 20; i++) {
                this.addItem(2020 + i);
            }

            this.addActionListener(e -> {
                Globals.Financial_Year = (int) this.getSelectedItem();
            });
        }
    }
}
