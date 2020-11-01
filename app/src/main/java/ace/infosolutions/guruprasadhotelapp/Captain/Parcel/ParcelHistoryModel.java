package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

public class ParcelHistoryModel {
    private String bill_no;
    private String customer_name;
    private String customer_contact;
    private boolean ishomedelivery;
    private String customer_address;
    private double subtotal;
    private double discount;
    private double total_cost;
    private String date_arrived;
    private String date_completed;
    private String time_arrived;
    private String time_completed;
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

    public ParcelHistoryModel(String bill_no, String customer_name, String customer_contact, boolean ishomedelivery, String customer_address, double subtotal, double discount, double total_cost, String date_arrived, String date_completed, String time_arrived, String time_completed, String payment_mode) {
        this.bill_no = bill_no;
        this.customer_name = customer_name;
        this.customer_contact = customer_contact;
        this.ishomedelivery = ishomedelivery;
        this.customer_address = customer_address;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total_cost = total_cost;
        this.date_arrived = date_arrived;
        this.date_completed = date_completed;
        this.time_arrived = time_arrived;
        this.time_completed = time_completed;
        this.payment_mode = payment_mode;
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

    public String getDate_completed() {
        return date_completed;
    }

    public String getTime_arrived() {
        return time_arrived;
    }

    public String getTime_completed() {
        return time_completed;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
