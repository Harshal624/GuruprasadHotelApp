package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

public class ParcelTallyPOJO {
    private String BILL_NO;
    private String cust_name;
    private String cust_address;
    private String payment_mode;
    private double discount;
    private double subtotal;
    private double total_cost;
    private String time_completed;
    private String cust_contact;

    public ParcelTallyPOJO(String BILL_NO, String cust_name, String cust_address, String payment_mode, double discount, double subtotal, double total_cost, String time_completed, String cust_contact) {
        this.BILL_NO = BILL_NO;
        this.cust_name = cust_name;
        this.cust_address = cust_address;
        this.payment_mode = payment_mode;
        this.discount = discount;
        this.subtotal = subtotal;
        this.total_cost = total_cost;
        this.time_completed = time_completed;
        this.cust_contact = cust_contact;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public String getCust_name() {
        return cust_name;
    }

    public String getCust_address() {
        return cust_address;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public double getDiscount() {
        return discount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public String getTime_completed() {
        return time_completed;
    }

    public String getCust_contact() {
        return cust_contact;
    }
}
