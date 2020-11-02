package ace.infosolutions.guruprasadhotelapp.Captain.Fish;

public class FoodMenuModel {
    private String item_title;
    private double item_cost;
    private String item_title_english;

    FoodMenuModel() {
    }

    public String getItem_title() {
        return item_title;
    }

    public double getItem_cost() {
        return item_cost;
    }

    public FoodMenuModel(String item_title, double item_cost, String item_title_english) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_title_english = item_title_english;
    }

    public String getItem_title_english() {
        return item_title_english;
    }
}
