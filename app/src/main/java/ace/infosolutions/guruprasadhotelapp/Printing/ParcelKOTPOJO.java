package ace.infosolutions.guruprasadhotelapp.Printing;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;

public class ParcelKOTPOJO {
    private String kot_no;
    private String date;
    private String time;
    private ArrayList<ViewCartModel> arrayList;
    private String customer_name;

    public ParcelKOTPOJO(String kot_no, String date, String time, ArrayList<ViewCartModel> arrayList, String customer_name) {
        this.kot_no = kot_no;
        this.date = date;
        this.time = time;
        this.arrayList = arrayList;
        this.customer_name = customer_name;
    }

    public String getKot_no() {
        return kot_no;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<ViewCartModel> getArrayList() {
        return arrayList;
    }

    public String getCustomer_name() {
        return customer_name;
    }
}
