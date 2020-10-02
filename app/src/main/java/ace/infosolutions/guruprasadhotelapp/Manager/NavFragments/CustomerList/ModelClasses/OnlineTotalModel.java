package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses;

public class OnlineTotalModel {
    private double onlinetotal;
    private String date;

    OnlineTotalModel(){}

    public double getOnlinetotal() {
        return onlinetotal;
    }

    public String getDate() {
        return date;
    }

    public OnlineTotalModel(double onlinetotal, String date) {
        this.onlinetotal = onlinetotal;
        this.date = date;
    }
}
