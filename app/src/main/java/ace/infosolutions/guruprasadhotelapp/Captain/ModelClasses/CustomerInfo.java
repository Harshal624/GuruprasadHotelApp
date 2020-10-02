package ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses;

public class CustomerInfo {
    private int table_no;
    private int no_of_cust;
    private String date_time;
    private String table_type;
    private double current_cost;
    private double confirmed_cost;

    public int getTable_no() {
        return table_no;
    }

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getTable_type() {
        return table_type;
    }

    public double getCurrent_cost() {
        return current_cost;
    }

    public double getConfirmed_cost() {
        return confirmed_cost;
    }

    public CustomerInfo(int table_no, int no_of_cust, String date_time, String table_type, double current_cost, double confirmed_cost) {
        this.table_no = table_no;
        this.no_of_cust = no_of_cust;
        this.date_time = date_time;
        this.table_type = table_type;
        this.current_cost = current_cost;
        this.confirmed_cost = confirmed_cost;
    }

    //no-arg constructor is needed
    public CustomerInfo() {
    }

}
