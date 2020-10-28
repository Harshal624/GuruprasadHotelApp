package ace.infosolutions.guruprasadhotelapp.Printing;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;

public class PrintingPOJO {
    private boolean iskot;
    private boolean isorder;
    private ArrayList<ViewCartModel> arrayList;
    private String table_no;
    private String table_type;
    private String date;
    private String time;
    private String bill_no;
    private String customer_name;
    private double subtotal;
    private double discount;
    private double total;
    private String customer_address;

    public PrintingPOJO(boolean iskot, boolean isorder, ArrayList<ViewCartModel> arrayList, String table_no, String table_type, String date, String time, String bill_no, String customer_name, double subtotal, double discount, double total, String customer_address) {
        this.iskot = iskot;
        this.isorder = isorder;
        this.arrayList = arrayList;
        this.table_no = table_no;
        this.table_type = table_type;
        this.date = date;
        this.time = time;
        this.bill_no = bill_no;
        this.customer_name = customer_name;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.customer_address = customer_address;
    }

    public PrintingPOJO(boolean iskot, boolean isorder, ArrayList<ViewCartModel> arrayList, String table_no, String table_type, String date, String time, String bill_no, String customer_name, double subtotal, double discount, double total) {
        this.iskot = iskot;
        this.isorder = isorder;
        this.arrayList = arrayList;
        this.table_no = table_no;
        this.table_type = table_type;
        this.date = date;
        this.time = time;
        this.bill_no = bill_no;
        this.customer_name = customer_name;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
    }

    public PrintingPOJO(boolean iskot, boolean isorder, ArrayList<ViewCartModel> arrayList, String table_no, String table_type, String date, String time, String bill_no) {
        this.iskot = iskot;
        this.isorder = isorder;
        this.arrayList = arrayList;
        this.table_no = table_no;
        this.table_type = table_type;
        this.date = date;
        this.time = time;
        this.bill_no = bill_no;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public boolean isIskot() {
        return iskot;
    }

    public boolean isIsorder() {
        return isorder;
    }

    public ArrayList<ViewCartModel> getArrayList() {
        return arrayList;
    }

    public String getTable_no() {
        return table_no;
    }

    public String getTable_type() {
        return table_type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getBill_no() {
        return bill_no;
    }
}
