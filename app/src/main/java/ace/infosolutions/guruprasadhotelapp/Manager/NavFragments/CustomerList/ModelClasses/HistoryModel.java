package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class HistoryModel {
    private String date_arrived;
    private String date_completed;
    private String time_arrived;
    private String time_completed;
    private String payment_mode;
    private double subtotal;
    private double discount;
    private double total_cost;
    private String table_type;
    private int table_no;
    private int no_of_cust;
    private String bill_no;

    HistoryModel() {
    }

    public HistoryModel(String date_arrived, String date_completed, String time_arrived, String time_completed, String payment_mode, double subtotal, double discount, double total_cost, String table_type, int table_no, int no_of_cust, String bill_no) {
        this.date_arrived = date_arrived;
        this.date_completed = date_completed;
        this.time_arrived = time_arrived;
        this.time_completed = time_completed;
        this.payment_mode = payment_mode;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total_cost = total_cost;
        this.table_type = table_type;
        this.table_no = table_no;
        this.no_of_cust = no_of_cust;
        this.bill_no = bill_no;
    }

    public String getDate_arrived() {
        return date_arrived;
    }

    public String getDate_completed() {
        return date_completed;
    }

    public String getTime_arrived() {
        return time_arrived;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public String getTime_completed() {
        return time_completed;
    }

    public double getSubtotal() {
        return subtotal;
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

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public String getBill_no() {
        return bill_no;
    }

    public double getDiscount() {
        return discount;
    }
}
