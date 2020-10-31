package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

public class DailyOrderTallyPOJO {
    private String BILL_NO;
    private int no_of_cust;
    private String payment_mode;
    private int table_no;
    private String table_type;
    private double discount;
    private double subtotal;
    private double total_cost;
    private String time_completed;

    public DailyOrderTallyPOJO(String BILL_NO, int no_of_cust, String payment_mode, int table_no, String table_type, double discount, double subtotal, double total_cost, String time_completed) {
        this.BILL_NO = BILL_NO;
        this.no_of_cust = no_of_cust;
        this.payment_mode = payment_mode;
        this.table_no = table_no;
        this.table_type = table_type;
        this.discount = discount;
        this.subtotal = subtotal;
        this.total_cost = total_cost;
        this.time_completed = time_completed;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public int getTable_no() {
        return table_no;
    }

    public String getTable_type() {
        return table_type;
    }

    public double getDiscount() {
        return discount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public String getTime_completed() {
        return time_completed;
    }
}
