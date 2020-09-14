package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.CustomerInfo;
import ace.infosolutions.guruprasadhotelapp.R;

public class AddCustomer extends AppCompatActivity {
    private Spinner table_type;
    private String selected_table="Family";
    private NumberPicker noofcustomers;
    private NumberPicker table_no;
    private Button confirmcust,cancelcust;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String KOT_NO;
    private String Bill_NO;
    private static final String TABLE_COLLECTION = "Tables";
    private static final String CUSTOMER_COLLECTION = "Customers";
    private ImageButton isoccupied;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String TABLE_TYPE_KEY = "TABLE_TYPE_KEY";
    private static final String TABLE_NO_KEY = "TABLE_NO_KEY";
    private SharedPreferences sharedPreferences;
    private boolean kotrequested = false;;

    private Map<String,Double> cost_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        table_type = (Spinner)findViewById(R.id.table_type);
        table_no = (NumberPicker) findViewById(R.id.table_no);
        noofcustomers = (NumberPicker) findViewById(R.id.noofcustomer);
        cancelcust = (Button) findViewById(R.id.cancelcust);
        confirmcust = (Button) findViewById(R.id.confirmcust);
        isoccupied = (ImageButton)findViewById(R.id.isoccupied);
        cost_map = new HashMap<>();
        sharedPreferences = getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        cost_map.put("cost",0.0);

        //setting up the spinner adapter
        ArrayAdapter<CharSequence> tabletypeadapter = ArrayAdapter.createFromResource(AddCustomer.this,R.array.Tabletype,android.R.layout.simple_spinner_item);
        tabletypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        table_type.setAdapter(tabletypeadapter);
        //max and min values for the numpicker
        noofcustomers.setMaxValue(6);
        noofcustomers.setMinValue(1);
        table_no.setMinValue(1);
        table_no.setMaxValue(12);
        //check table availablility when nothing is selected
        checkTableAvailability(selected_table, String.valueOf(table_no.getValue()));
        //check table availability when the numpicker value is changed
        checkOnValuechanged();


        table_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_table =adapterView.getItemAtPosition(i).toString();
                if(selected_table.equals("Family")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(12);
                }
                else if(selected_table.equals("AC Family")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(5);
                    checkTableAvailability(selected_table, String.valueOf(table_no.getValue()));
                }
               else if(selected_table.equals("Bar Dining")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(12);
                    checkTableAvailability(selected_table, String.valueOf(table_no.getValue()));
                }
                else if(selected_table.equals("VIP Dining")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(5);
                    checkTableAvailability(selected_table, String.valueOf(table_no.getValue()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_table = "Family";
                table_no.setMinValue(1);
                table_no.setMaxValue(12);

            }
        });

        confirmcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection()){
                    confirmcust.setEnabled(false);
                    addDatatoFirebase();
                }
                else{
                    confirmcust.setEnabled(false);
                    Toast.makeText(AddCustomer.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

                }

        });

        cancelcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(AddCustomer.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOnValuechanged() {
        try{
            table_no.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(final NumberPicker numberPicker, int i, int i1) {
                    db.collection("Tables").document(selected_table).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String table_noFree = String.valueOf(numberPicker.getValue());
                                    checkTableAvailability(selected_table,table_noFree);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Failed ",e.toString());
                        }
                    });
                }
            });

        }
        catch (NullPointerException e){
            Log.e("Nullpointeroutsideloop",e.toString());
        }
    }

    private void checkTableAvailability(String selectedtable, final String table_no) {
        db.collection("Tables").document(selectedtable).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try{
                            boolean istablefree = documentSnapshot.getBoolean(table_no);
                            if(!istablefree){
                                isoccupied.setImageResource(R.drawable.ic_occupied);
                                confirmcust.setEnabled(false);
                            }
                            else{
                                isoccupied.setImageResource(R.drawable.ic_free);
                                confirmcust.setEnabled(true);
                            }
                        }
                        catch (NullPointerException e){
                           // istablefreeT.setText("Checking");
                            confirmcust.setEnabled(false);
                            Log.e("Nullpointer",e.toString());
                        }
                        catch (RuntimeException e){
                            Log.e("Runtime",e.toString());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failed ",e.toString());

            }
        });

    }

    private void addDatatoFirebase() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        String datetoday = format.format(date);
        double cost = 0;
        //creating customer info object
       CustomerInfo customerInfo = new CustomerInfo(table_no.getValue(),noofcustomers.getValue(),datetoday,selected_table,cost,kotrequested);
       //adding data to firebase
       db.collection(CUSTOMER_COLLECTION).add(customerInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
           @Override
           public void onSuccess(DocumentReference documentReference) {
               //TODO ALSO TABLE_TYPE AND TABLE NO IN SHAREDPREFERENCES
               final String doc_id = documentReference.getId();
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putString(DOC_ID_KEY,doc_id);
               editor.putInt(TABLE_NO_KEY,table_no.getValue());
               editor.putString(TABLE_TYPE_KEY,selected_table);
               editor.commit();

               db.collection(TABLE_COLLECTION).document(selected_table).update(String.valueOf(table_no.getValue()),false).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       db.collection(CUSTOMER_COLLECTION).document(doc_id).collection("COST")
                               .document("COST")
                               .set(cost_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(AddCustomer.this, "Successfully added", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(getApplicationContext(),FoodMenu.class);
                               startActivity(intent);

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(AddCustomer.this, "Failed to add", Toast.LENGTH_SHORT).show();
                           }
                       });

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddCustomer.this, "Failed to add", Toast.LENGTH_SHORT).show();
                   }
               });


           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {

           }
       });
    }

   private boolean haveNetworkConnection() {
       boolean haveConnectedWifi = false;
       boolean haveConnectedMobile = false;

       ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo[] netInfo = cm.getAllNetworkInfo();
       for (NetworkInfo ni : netInfo) {
           if (ni.getTypeName().equalsIgnoreCase("WIFI"))
               if (ni.isConnected())
                   haveConnectedWifi = true;
           if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
               if (ni.isConnected())
                   haveConnectedMobile = true;
       }
       return haveConnectedWifi || haveConnectedMobile;
   }

}
