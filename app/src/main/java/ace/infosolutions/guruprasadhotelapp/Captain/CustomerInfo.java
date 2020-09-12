package ace.infosolutions.guruprasadhotelapp.Captain;

//POJO to add a new customer

import java.util.Map;

public class CustomerInfo {
    private int table_no;
    private int no_of_cust;
    private String date_time;
    private String table_type;


    //no-arg constructor is needed
    public CustomerInfo() {
    }

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

    public CustomerInfo(int table_no, int no_of_cust, String date_time, String table_type) {
        this.table_no = table_no;
        this.no_of_cust = no_of_cust;
        this.date_time = date_time;
        this.table_type = table_type;
    }
}
