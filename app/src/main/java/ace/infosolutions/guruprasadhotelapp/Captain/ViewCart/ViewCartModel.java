package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

public class ViewCartModel {
    private String item_title;
    private double item_cost;
    private int item_qty;

    public ViewCartModel() {
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

    public ViewCartModel(String item_title, double item_cost, int item_qty) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_qty = item_qty;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public void setItem_cost(double item_cost) {
        this.item_cost = item_cost;
    }

    public void setItem_qty(int item_qty) {
        this.item_qty = item_qty;
    }
}