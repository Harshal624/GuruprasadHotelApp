package ace.infosolutions.guruprasadhotelapp.Captain;



public class CustomerInfo {
    private int table_no;
    private int no_of_cust;
    private String date_time;
    private boolean order_complete;
    private boolean isorder;
    private String table_type;


    public CustomerInfo() {
    }

    public CustomerInfo(int table_no, int no_of_cust, String date_time, boolean order_complete, boolean isorder, String table_type) {
        this.table_no = table_no;
        this.no_of_cust = no_of_cust;
        this.date_time = date_time;
        this.order_complete = order_complete;
        this.isorder = isorder;
        this.table_type = table_type;
    }

    public int getTable_no() {
        return table_no;
    }

    public int getNo_of_cust() {
        return no_of_cust;
    }

    public String getTable_type() {
        return table_type;
    }

    public String getDate_time() {
        return date_time;
    }

    public boolean isOrder_complete() {
        return order_complete;
    }

    public boolean isIsorder() {
        return isorder;
    }
}
