package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class ParcelTotalModel {
    private double parceltotal;
    private String date;

    ParcelTotalModel() {
    }

    public double getParceltotal() {
        return parceltotal;
    }

    public String getDate() {
        return date;
    }

    public ParcelTotalModel(double parceltotal, String date) {
        this.parceltotal = parceltotal;
        this.date = date;
    }
}
