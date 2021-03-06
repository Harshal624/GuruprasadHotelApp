package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.CustomerInfo;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;
import ace.infosolutions.guruprasadhotelapp.Utils.GenerateNumber;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

public class AddCustomer extends AppCompatActivity {
    private Button confirm_button, cancel_button;
    private NumberPicker table_noNP, noofcustNP;
    private RadioGroup table_typeRG;
    private ImageButton isavailable;
    private InternetConn conn;
    private FirebaseFirestore db;
    private RadioButton radioButton;
    private Map<String, Object> cost;
    private SharedPreferences preferences;
    private String table_typeString = "VIP Dining";
    private int table_noInt = 1;
    private CollectionReference customerRef, tableRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        db = FirebaseFirestore.getInstance();
        confirm_button = findViewById(R.id.confirmcust);
        cancel_button = findViewById(R.id.cancelcust);
        table_noNP = findViewById(R.id.tablenonumpicker);
        noofcustNP = findViewById(R.id.noofcustnumpicker);
        table_typeRG = findViewById(R.id.radiogroup);
        isavailable = findViewById(R.id.tableavailimgbtn);
        conn = new InternetConn(this);
        cost = new HashMap<>();
        cost.put("cost", 0);

        preferences = getSharedPreferences(Constants.PREF_DOCID, Context.MODE_PRIVATE);
        customerRef = db.collection(Constants.CUSTOMERS);
        tableRef = db.collection(Constants.TABLES);

        table_noNP.setMaxValue(20);
        table_noNP.setMinValue(1);
        noofcustNP.setMaxValue(6);
        noofcustNP.setMinValue(1);

        checkIfTableisAvail(table_typeString, table_noInt);

        RadioButton vip_dining = findViewById(R.id.vipdiningradio);
        vip_dining.setTextColor(getResources().getColor(R.color.colorAccent));

        table_typeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                table_typeString = radioButton.getText().toString();
                updateRadioButtonColor(table_typeString);
                if (table_typeString.equals("Family")) {
                    table_noNP.setMaxValue(20);
                } else if (table_typeString.equals("AC Family")) {
                    table_noNP.setMaxValue(20);
                } else if (table_typeString.equals("Bar Dining"))
                    table_noNP.setMaxValue(20);
                else if (table_typeString.equals("VIP Dining"))
                    table_noNP.setMaxValue(20);
                checkIfTableisAvail(table_typeString, table_noInt);
            }

            private void updateRadioButtonColor(String table_typeString) {
                RadioButton vip = findViewById(R.id.vipdiningradio);
                RadioButton family = findViewById(R.id.familyradio);
                RadioButton ac_family = findViewById(R.id.acfamilyradio);
                RadioButton bar = findViewById(R.id.bardiningradio);

                if (table_typeString.equals("Family")) {
                    family.setTextColor(getResources().getColor(R.color.colorAccent));
                    vip.setTextColor(getResources().getColor(R.color.black));
                    ac_family.setTextColor(getResources().getColor(R.color.black));
                    bar.setTextColor(getResources().getColor(R.color.black));
                } else if (table_typeString.equals("AC Family")) {
                    family.setTextColor(getResources().getColor(R.color.black));
                    vip.setTextColor(getResources().getColor(R.color.black));
                    ac_family.setTextColor(getResources().getColor(R.color.colorAccent));
                    bar.setTextColor(getResources().getColor(R.color.black));
                } else if (table_typeString.equals("Bar Dining")) {
                    family.setTextColor(getResources().getColor(R.color.black));
                    vip.setTextColor(getResources().getColor(R.color.black));
                    ac_family.setTextColor(getResources().getColor(R.color.black));
                    bar.setTextColor(getResources().getColor(R.color.colorAccent));

                } else if (table_typeString.equals("VIP Dining")) {

                    family.setTextColor(getResources().getColor(R.color.black));
                    vip.setTextColor(getResources().getColor(R.color.colorAccent));
                    ac_family.setTextColor(getResources().getColor(R.color.black));
                    bar.setTextColor(getResources().getColor(R.color.black));

                }
            }
        });


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conn.haveNetworkConnection()) {
                    confirm_button.setEnabled(false);
                    addCustomer();
                } else
                    Toast.makeText(AddCustomer.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

        table_noNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                table_noInt = numberPicker.getValue();
                checkIfTableisAvail(table_typeString, table_noInt);
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
                overridePendingTransition(0, 0);
            }
        });

    }

    private void checkIfTableisAvail(final String table_type, final int table_no) {
        db.collection(Constants.TABLES).document(table_type).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean isavail = false;
                    try {
                        isavail = task.getResult().getBoolean(String.valueOf(table_no));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        checkIfTableisAvail(table_type, 5);
                    }
                    if (isavail) {
                        confirm_button.setEnabled(true);
                        isavailable.setImageResource(R.drawable.ic_free);
                    } else {
                        confirm_button.setEnabled(false);
                        isavailable.setImageResource(R.drawable.ic_occupied);
                    }
                }
            }
        });

    }

    private void addCustomer() {
        double final_cost = 0;
        double current_cost = 0;
        GenerateNumber number = new GenerateNumber();
        String BILL_NO = number.generateBillNo();
        String date_arrived = number.generateDateOnly();
        String arrived_time = number.generateTimeOnly();
        int no_cust = noofcustNP.getValue();
        CustomerInfo customerInfo = new CustomerInfo(table_noInt, no_cust, date_arrived, arrived_time, table_typeString, final_cost, current_cost, BILL_NO, 0.0, 0.0, false);

        final DocumentReference reference = db.collection(Constants.CUSTOMERS).document();
        WriteBatch batch = db.batch();
        batch.set(reference, customerInfo);
        DocumentReference tableRef = db.collection("Tables").document(table_typeString);
        batch.update(tableRef, String.valueOf(table_noInt), false);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.DOC_ID_KEY, reference.getId());
                editor.commit();
                Toast.makeText(AddCustomer.this, "Added", Toast.LENGTH_SHORT).show();
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), FoodMenu.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}