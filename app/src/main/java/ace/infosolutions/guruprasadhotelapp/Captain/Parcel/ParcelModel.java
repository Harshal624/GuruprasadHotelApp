package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

public class ParcelModel {
    private String customer_name;
    private String customer_contact;
    private boolean ishomedelivery;
    private String customer_address;
    private double current_cost;
    private double confirmed_cost;
    private String date_time;


    ParcelModel() {
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_contact() {
        return customer_contact;
    }

    public boolean isIshomedelivery() {
        return ishomedelivery;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public double getCurrent_cost() {
        return current_cost;
    }

    public double getConfirmed_cost() {
        return confirmed_cost;
    }

    public String getDate_time() {
        return date_time;
    }

    public ParcelModel(String customer_name, String customer_contact, boolean ishomedelivery, String customer_address, double current_cost, double confirmed_cost, String date_time) {
        this.customer_name = customer_name;
        this.customer_contact = customer_contact;
        this.ishomedelivery = ishomedelivery;
        this.customer_address = customer_address;
        this.current_cost = current_cost;
        this.confirmed_cost = confirmed_cost;
        this.date_time = date_time;
    }
}
