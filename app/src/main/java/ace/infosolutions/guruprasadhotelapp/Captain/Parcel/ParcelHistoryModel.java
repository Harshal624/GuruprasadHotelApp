package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

public class ParcelHistoryModel {
    private String bill_no;
    private String customer_name;
    private String customer_contact;
    private boolean ishomedelivery;
    private String customer_address;
    private double confirmed_cost;
    private String date_time_arrived;
    private String date_time_completed;
    private String payment_mode;

    ParcelHistoryModel() {

    }

    public String getBill_no() {
        return bill_no;
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

    public double getConfirmed_cost() {
        return confirmed_cost;
    }

    public String getDate_time_arrived() {
        return date_time_arrived;
    }

    public String getDate_time_completed() {
        return date_time_completed;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public ParcelHistoryModel(String bill_no, String customer_name, String customer_contact, boolean ishomedelivery, String customer_address, double confirmed_cost, String date_time_arrived, String date_time_completed, String payment_mode) {
        this.bill_no = bill_no;
        this.customer_name = customer_name;
        this.customer_contact = customer_contact;
        this.ishomedelivery = ishomedelivery;
        this.customer_address = customer_address;
        this.confirmed_cost = confirmed_cost;
        this.date_time_arrived = date_time_arrived;
        this.date_time_completed = date_time_completed;
        this.payment_mode = payment_mode;
    }
}
