package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class FinalBillModel {
    private String item_title;
    private double item_cost;
    private int item_qty;

    FinalBillModel() {
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

    public FinalBillModel(String item_title, double item_cost, int item_qty) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_qty = item_qty;
    }
}
