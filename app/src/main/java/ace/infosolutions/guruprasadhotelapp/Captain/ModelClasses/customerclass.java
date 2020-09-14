package ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses;

//POJO to read firestore data

public class customerclass {
    private int table_no;
    private double cost;
    private String table_type;

    //no-arg constructor is needed
    public customerclass() {

    }

    public customerclass(int table_no, double cost, String table_type) {
        this.table_no = table_no;
        this.cost = cost;
        this.table_type = table_type;
    }

    public int getTable_no() {
        return table_no;
    }

    public double getCost() {
        return cost;
    }

    public String getTable_type() {
        return table_type;
    }
}
