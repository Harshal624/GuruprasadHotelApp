package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class TableTotalModel {
    private double tabletotal;
    private int table_no;

    TableTotalModel() {
    }

    public double getTabletotal() {
        return tabletotal;
    }

    public int getTable_no() {
        return table_no;
    }

    public TableTotalModel(double tabletotal, int table_no) {
        this.tabletotal = tabletotal;
        this.table_no = table_no;
    }
}

