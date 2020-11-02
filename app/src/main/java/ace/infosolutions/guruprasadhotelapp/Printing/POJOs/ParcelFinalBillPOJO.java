package ace.infosolutions.guruprasadhotelapp.Printing.POJOs;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;

public class ParcelFinalBillPOJO {
    private String bill_no;
    private String date;
    private String time;
    private String customer_name;
    private String customer_address;
    private String customer_contact;
    private String subtotal;
    private String discount;
    private String total_cost;
    private ArrayList<ViewCartModel> arrayList;

    public ParcelFinalBillPOJO(String bill_no, String date, String time, String customer_name, String customer_address, String customer_contact, String subtotal, String discount, String total_cost, ArrayList<ViewCartModel> arrayList) {
        this.bill_no = bill_no;
        this.date = date;
        this.time = time;
        this.customer_name = customer_name;
        this.customer_address = customer_address;
        this.customer_contact = customer_contact;
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

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
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

    public String getCustomer_contact() {
        return customer_contact;
    }
}
