package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

public class HistoryModel {
    private String item_title;
    private double item_cost;
    private int item_qty;
    private boolean isconfirmed;

    HistoryModel(){}

    public HistoryModel(String item_title, double item_cost, int item_qty, boolean isconfirmed) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_qty = item_qty;
        this.isconfirmed = isconfirmed;
    }

    public String getItem_title() {
        return item_title;
    }

    public double getItem_cost() {
        return item_cost;
    }

    public int getItem_qty() {
        return item_qty;
    }

    public boolean isIsconfirmed() {
        return isconfirmed;
    }
}
