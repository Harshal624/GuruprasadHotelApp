package ace.infosolutions.guruprasadhotelapp.Captain.Adapters;

public class FoodItemModel {
    private String item_title;
    private double item_cost;
    private int item_qty;
    private String item_title_english;

    FoodItemModel() {
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

    public FoodItemModel(String item_title, double item_cost, int item_qty, String item_title_english) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_qty = item_qty;
        this.item_title_english = item_title_english;
    }

    public String getItem_title_english() {
        return item_title_english;
    }
}
