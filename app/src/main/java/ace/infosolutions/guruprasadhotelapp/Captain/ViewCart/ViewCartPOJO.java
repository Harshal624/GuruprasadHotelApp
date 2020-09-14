package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

public class ViewCartPOJO {
    private String item_title;
    private int item_cost;
    private int item_qty;

    public ViewCartPOJO() {
    }

    public String getItem_title() {
        return item_title;
    }

    public int getItem_cost() {
        return item_cost;
    }

    public int getItem_qty() {
        return item_qty;
    }

    public ViewCartPOJO(String item_title, int item_cost, int item_qty) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_qty = item_qty;
    }
}