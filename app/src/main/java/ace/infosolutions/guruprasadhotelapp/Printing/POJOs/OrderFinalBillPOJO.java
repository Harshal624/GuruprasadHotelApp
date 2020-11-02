package ace.infosolutions.guruprasadhotelapp.Printing.POJOs;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;

public class OrderFinalBillPOJO {
    private String bill_no;
    private String date;
    private String time;
    private String table_no;
    private String table_type;
    private String subtotal;
    private String discount;
    private String total_cost;
    private ArrayList<ViewCartModel> arrayList;

    public OrderFinalBillPOJO(String bill_no, String date, String time, String table_no, String table_type, String subtotal, String discount, String total_cost, ArrayList<ViewCartModel> arrayList) {
        this.bill_no = bill_no;
        this.date = date;
        this.time = time;
        this.table_no = table_no;
        this.table_type = table_type;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total_cost = total_cost;
        this.arrayList = arrayList;
    }

    public String getBill_no() {
        return bill_no;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTable_no() {
        return table_no;
    }

    public String getTable_type() {
        return table_type;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public ArrayList<ViewCartModel> getArrayList() {
        return arrayList;
    }
}
