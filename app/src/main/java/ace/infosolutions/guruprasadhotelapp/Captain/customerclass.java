package ace.infosolutions.guruprasadhotelapp.Captain;

import android.widget.TextView;

public class customerclass {
    private int table_no;
    private double cost;

    public customerclass(int table_no, double cost) {
        this.table_no = table_no;
        this.cost = cost;
    }

    public int getTable_no() {
        return table_no;
    }

    public double getCost() {
        return cost;
    }
}
