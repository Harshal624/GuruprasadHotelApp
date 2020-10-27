package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class HistoryModel {
    private String date_time_arrived;
    private String payment_mode;
    private double total_cost;
    private String table_type;
    private int table_no;
    private String date_time_completed;
    private int no_of_cust;
    private String bill_no;

    HistoryModel() {
    }

    public HistoryModel(String date_time_arrived, String payment_mode, double total_cost, String table_type, int table_no, String date_time_completed, int no_of_cust, String bill_no) {
        this.date_time_arrived = date_time_arrived;
        this.payment_mode = payment_mode;
        this.total_cost = total_cost;
        this.table_type = table_type;
        this.table_no = table_no;
        this.date_time_completed = date_time_completed;
        this.no_of_cust = no_of_cust;
        this.bill_no = bill_no;
    }

    public HistoryModel(String date_time_arrived, String payment_mode, double total_cost, String table_type, int table_no, String date_time_completed, int no_of_cust) {
        this.date_time_arrived = date_time_arrived;
        this.payment_mode = payment_mode;
        this.total_cost = total_cost;
        this.table_type = table_type;
        this.table_no = table_no;
        this.date_time_completed = date_time_completed;
        this.no_of_cust = no_of_cust;
    }

    public String getDate_time_arrived() {
        return date_time_arrived;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public String getTable_type() {
        return table_type;
    }

    public int getTable_no() {
        return table_no;
    }

    public String getDate_time_completed() {
        return date_time_completed;
    }

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public String getBill_no() {
        return bill_no;
    }
}
