package ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses;

public class customerclass {
    private int table_no;
    private double confirmed_cost;
    private String table_type;
    private boolean isconfirmed;

    public customerclass(int table_no, double confirmed_cost, String table_type, boolean isonfirmed) {
        this.table_no = table_no;
        this.confirmed_cost = confirmed_cost;
        this.table_type = table_type;
        this.isconfirmed = isonfirmed;
    }

    public boolean isIsconfirmed() {
        return isconfirmed;
    }

    public customerclass() {

    }

    public int getTable_no() {
        return table_no;
    }

    public double getConfirmed_cost() {
        return confirmed_cost;
    }

    public String getTable_type() {
        return table_type;
    }

    public customerclass(int table_no, double confirmed_cost, String table_type) {
        this.table_no = table_no;
        this.confirmed_cost = confirmed_cost;
        this.table_type = table_type;
    }
}
