package ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses;

public class CustomerInfo {
    private int table_no;
    private int no_of_cust;
    private String date_arrived;
    private String time_arrived;
    private String table_type;
    private double current_cost;
    private double confirmed_cost;
    private String bill_no;
    private double discount;
    private double total_cost;

    public int getTable_no() {
        return table_no;
    }

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public CustomerInfo(int table_no, int no_of_cust, String date_arrived, String time_arrived, String table_type, double current_cost, double confirmed_cost, String bill_no, double discount, double total_cost) {
        this.table_no = table_no;
        this.no_of_cust = no_of_cust;
        this.date_arrived = date_arrived;
        this.time_arrived = time_arrived;
        this.table_type = table_type;
        this.current_cost = current_cost;
        this.confirmed_cost = confirmed_cost;
        this.bill_no = bill_no;
        this.discount = discount;
        this.total_cost = total_cost;
    }

    public String getDate_arrived() {
        return date_arrived;
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

    public String getTime_arrived() {
        return time_arrived;
    }

    public String getBill_no() {
        return bill_no;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    //no-arg constructor is needed
    public CustomerInfo() {
    }

}
