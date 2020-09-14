package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

public class requestedkotmodel {
    private boolean kotrequested;
    private String table_type;
    private int table_no;


    requestedkotmodel() {

    }

    public boolean isKotrequested() {
        return kotrequested;
    }

    public String getTable_type() {
        return table_type;
    }

    public int getTable_no() {
        return table_no;
    }

    public requestedkotmodel(boolean kotrequested, String table_type, int table_no) {
        this.kotrequested = kotrequested;
        this.table_type = table_type;
        this.table_no = table_no;
    }
}

