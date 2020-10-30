package ace.infosolutions.guruprasadhotelapp.Printing;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;

public class OrderKOTPOJO {
    private String kot_no;
    private String date;
    private String time;
    private ArrayList<ViewCartModel> arrayList;
    private String table_no;
    private String table_type;


    public OrderKOTPOJO(String kot_no, String date, String time, ArrayList<ViewCartModel> arrayList, String table_no, String table_type) {
        this.kot_no = kot_no;
        this.date = date;
        this.time = time;
        this.arrayList = arrayList;
        this.table_no = table_no;
        this.table_type = table_type;
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

    public String getTable_no() {
        return table_no;
    }

    public String getTable_type() {
        return table_type;
    }
}
