package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

public class ParcelModel {
    private String customer_name;
    private String customer_contact;
    private boolean ishomedelivery;
    private String customer_address;
    private double current_cost;
    private double confirmed_cost;
    private double discount;
    private double total_cost;
    private String date_arrived;
    private String time_arrived;
    private String bill_no;

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

    public ParcelModel(String customer_name, String customer_contact, boolean ishomedelivery, String customer_address, double current_cost, double confirmed_cost, double discount, double total_cost, String date_arrived, String time_arrived, String bill_no) {
        this.customer_name = customer_name;
        this.customer_contact = customer_contact;
        this.ishomedelivery = ishomedelivery;
        this.customer_address = customer_address;
        this.current_cost = current_cost;
        this.confirmed_cost = confirmed_cost;
        this.discount = discount;
        this.total_cost = total_cost;
        this.date_arrived = date_arrived;
        this.time_arrived = time_arrived;
        this.bill_no = bill_no;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public String getDate_arrived() {
        return date_arrived;
    }

    public String getTime_arrived() {
        return time_arrived;
    }

    public String getBill_no() {
        return bill_no;
    }
}
