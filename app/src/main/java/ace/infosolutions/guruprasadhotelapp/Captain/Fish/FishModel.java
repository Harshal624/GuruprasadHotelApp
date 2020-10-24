package ace.infosolutions.guruprasadhotelapp.Captain.Fish;

public class FishModel {
    private String item_title;
    private double item_cost;

    FishModel() {
    }

    public String getItem_title() {
        return item_title;
    }

    public double getItem_cost() {
        return item_cost;
    }

    public FishModel(String item_title, int item_cost) {
        this.item_title = item_title;
        this.item_cost = item_cost;
    }
}
