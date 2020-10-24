package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class GrandTotalModel {
    private double grandtotal;
    private String date;

    GrandTotalModel() {
    }

    public GrandTotalModel(double grandtotal, String date) {
        this.grandtotal = grandtotal;
        this.date = date;
    }

    public double getGrandtotal() {
        return grandtotal;
    }

    public String getDate() {
        return date;
    }
}
