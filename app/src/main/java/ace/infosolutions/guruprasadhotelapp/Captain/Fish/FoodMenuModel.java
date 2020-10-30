package ace.infosolutions.guruprasadhotelapp.Captain.Fish;

public class FoodMenuModel {
    private String item_title;
    private double item_cost;

    FoodMenuModel() {
    }

    public String getItem_title() {
        return item_title;
    }

    public double getItem_cost() {
        return item_cost;
    }

    public FoodMenuModel(String item_title, int item_cost) {
        this.item_title = item_title;
        this.item_cost = item_cost;
    }
}
