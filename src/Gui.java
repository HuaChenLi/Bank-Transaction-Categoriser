package src;

import src.Panels.*;

public class Gui {
    static MappingPanel mappingPanel;
    static ExcelColumnViewPanel excelColumnViewPanel;
    public Gui() {
    }
    public static void refresh() {
        mappingPanel.refreshMappingTable();
        excelColumnViewPanel.refreshAll();

    }
    static public void setMappingPanel(MappingPanel p) {
        mappingPanel = p;
    }
    static public void setExcelColumnViewPanel(ExcelColumnViewPanel p) {
        excelColumnViewPanel = p;
    }
}
