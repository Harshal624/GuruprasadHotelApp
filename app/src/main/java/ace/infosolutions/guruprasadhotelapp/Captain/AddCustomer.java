package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView istablefreeT;
    private static final String TABLE_COLLECTION = "Tables";
    private static final String CUSTOMER_COLLECTION = "Customers";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        table_type = (Spinner)findViewById(R.id.table_type);
        table_no = (NumberPicker) findViewById(R.id.table_no);
        noofcustomers = (NumberPicker) findViewById(R.id.noofcustomer);
        cancelcust = (Button) findViewById(R.id.cancelcust);
        confirmcust = (Button) findViewById(R.id.confirmcust);
        istablefreeT = (TextView)findViewById(R.id.istablefree);

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
                    addDatatoFirebase();
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
            istablefreeT.setText("Checking");
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
                            Log.e("istablefree", String.valueOf(istablefree));
                            if(!istablefree){
                                istablefreeT.setText("Occupied");
                                confirmcust.setEnabled(false);
                            }
                            else{
                                istablefreeT.setText("Free");
                                confirmcust.setEnabled(true);
                            }
                        }
                        catch (NullPointerException e){
                            istablefreeT.setText("Checking");
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
        //creating unique document id for table number
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        final String tableno = "T"+table_no.getValue();
        String datetoday = format.format(date);
        String day = datetoday.substring(0,2).concat(datetoday.substring(3,5)).concat(datetoday.substring(6,8));
        String time = datetoday.substring(datetoday.length() - 8);
        String doc_table_no = day.concat(time).concat(tableno);
        Log.e("doctable",doc_table_no);
        //creating customer info object
       CustomerInfo customerInfo = new CustomerInfo(table_no.getValue(),noofcustomers.getValue(),datetoday,true,selected_table);
       //adding data to firebase
       db.collection(CUSTOMER_COLLECTION).document(doc_table_no).set(customerInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {

               db.collection(TABLE_COLLECTION).document(selected_table).update(String.valueOf(table_no.getValue()),false).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText(AddCustomer.this, "Added", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(getApplicationContext(),FoodMenu.class));
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddCustomer.this, "Failed", Toast.LENGTH_SHORT).show();

                   }
               });
               finish();

           }

       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(AddCustomer.this, "Failed", Toast.LENGTH_SHORT).show();

           }
       });

    }
   //Logic of generating KOT number
   /* public void generateKOT() {
        Date date = new Date();
        Random r = new Random();
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 3);

        char c = (char)(r.nextInt(26) + 'a');
        String c1 = String.valueOf(c).toUpperCase();
        KOT_NO = c1.concat(randomMilli);
        Log.e("random", KOT_NO);

    }
    //Logic of generating Bill no of the customer
    public void generateBillNO(){
        Random r = new Random();
        Date date = new Date();
        char a = (char)(r.nextInt(26) + 'a');
        char b = (char)(r.nextInt(26) + 'a');
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 5);
        Bill_NO = String.valueOf(a).concat(String.valueOf(b)).toUpperCase().concat(randomMilli);
        Log.e("billno", Bill_NO);
    }*/
}
