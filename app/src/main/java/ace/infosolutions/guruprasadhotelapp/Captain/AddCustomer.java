package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.CustomerInfo;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

public class AddCustomer extends AppCompatActivity {
    private static final String TABLES ="Tables";
    private static final String CUSTOMERS = "Customers";
    private static final String COST = "COST";
    private Button confirm_button, cancel_button;
    private NumberPicker table_noNP, noofcustNP;
    private RadioGroup table_typeRG;
    private ImageButton isavailable;
    private InternetConn conn;
    private FirebaseFirestore db;
    private RadioButton radioButton;
    private Map<String,Object> cost;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private SharedPreferences preferences;
    private String doc_id;
    private String table_typeString = "VIP Dining";
    private int table_noInt = 1;
    private CollectionReference customerRef,tableRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        db = FirebaseFirestore.getInstance();
        confirm_button = (Button) findViewById(R.id.confirmcust);
        cancel_button = (Button) findViewById(R.id.cancelcust);
        table_noNP = (NumberPicker) findViewById(R.id.tablenonumpicker);
        noofcustNP = (NumberPicker)findViewById(R.id.noofcustnumpicker);
        table_typeRG = (RadioGroup) findViewById(R.id.radiogroup);
        isavailable = (ImageButton) findViewById(R.id.tableavailimgbtn);
        conn = new InternetConn(this);
        cost = new HashMap<>();
        cost.put("cost",0);

        preferences = getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        customerRef= db.collection(CUSTOMERS);
        tableRef = db.collection(TABLES);

        table_noNP.setMaxValue(5);
        table_noNP.setMinValue(1);
        noofcustNP.setMaxValue(6);
        noofcustNP.setMinValue(1);

        checkIfTableisAvail(table_typeString,table_noInt);

        table_typeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = (RadioButton) findViewById(i);
                table_typeString = radioButton.getText().toString();
                if(table_typeString.equals("Family"))
                    table_noNP.setMaxValue(12);
                else if(table_typeString.equals("AC Family"))
                    table_noNP.setMaxValue(5);
                else if(table_typeString.equals("Bar Dining"))
                    table_noNP.setMaxValue(12);
                else if(table_typeString.equals("VIP Dining"))
                    table_noNP.setMaxValue(5);
                checkIfTableisAvail(table_typeString,table_noInt);
            }
        });


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conn.haveNetworkConnection()){
                    confirm_button.setEnabled(false);
                    addCustomer();
                }

                else
                    Toast.makeText(AddCustomer.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

        table_noNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                 table_noInt = numberPicker.getValue();
                 checkIfTableisAvail(table_typeString,table_noInt);
            }
        });

    }

    private void checkIfTableisAvail(final String table_type, final int table_no) {
        db.collection(TABLES).document(table_type).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    boolean isavail= false;
                    try {
                        isavail = task.getResult().getBoolean(String.valueOf(table_no));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        checkIfTableisAvail(table_type,5);
                    }
                    if(isavail){
                        confirm_button.setEnabled(true);
                        isavailable.setImageResource(R.drawable.ic_free);
                    }
                    else{
                        confirm_button.setEnabled(false);
                        isavailable.setImageResource(R.drawable.ic_occupied);
                    }
                }
            }
        });

    }

    private void addCustomer() {
        double cost=0;
        boolean kotreqested = false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        String datetoday = format.format(date);
        int no_cust = noofcustNP.getValue();
        CustomerInfo customerInfo = new CustomerInfo(table_noInt,no_cust,datetoday,table_typeString,cost,kotreqested);
        customerRef.add(customerInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    doc_id = task.getResult().getId();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(DOC_ID_KEY,doc_id);
                    editor.commit();
                    updateTableStatus(doc_id);
                }
            }
        });

    }

    private void updateTableStatus(final String id) {
       tableRef.document(table_typeString).update(String.valueOf(table_noInt),false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    addCostSubcollection(id);
                }
            }
        });
    }

    private void addCostSubcollection(String id) {
        customerRef.document(id).collection(COST).document(COST).set(cost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddCustomer.this, "Added", Toast.LENGTH_SHORT).show();
                finishAffinity();
                startActivity(new Intent(getApplicationContext(),FoodMenu.class));
                overridePendingTransition(0,0);
            }
        });
    }

}
