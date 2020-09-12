package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

public class ViewCartPOJO {
    private String food_title;
    private int food_cost;
    private int food_qty;

    public ViewCartPOJO() {
    }

    public ViewCartPOJO(String food_title, int food_cost, int food_qty) {
        this.food_title = food_title;
        this.food_cost = food_cost;
        this.food_qty = food_qty;
    }

    public String getFood_title() {
        return food_title;
    }

    public int getFood_cost() {
        return food_cost;
    }

    public int getFood_qty() {
        return food_qty;
    }
}