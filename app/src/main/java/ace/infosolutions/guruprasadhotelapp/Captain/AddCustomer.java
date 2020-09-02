package ace.infosolutions.guruprasadhotelapp.Captain;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ace.infosolutions.guruprasadhotelapp.R;

public class AddCustomer extends AppCompatActivity {
    private Spinner table_type;
    private String selected_table;
    private NumberPicker noofcustomers;
    private NumberPicker table_no;
    private Button confirmcust,cancelcust;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String KOT_NO;
    private String Bill_NO;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        table_type = (Spinner)findViewById(R.id.table_type);
        table_no = (NumberPicker) findViewById(R.id.table_no);
        noofcustomers = (NumberPicker) findViewById(R.id.noofcustomer);
        cancelcust = (Button) findViewById(R.id.cancelcust);
        confirmcust = (Button) findViewById(R.id.confirmcust);


        ArrayAdapter<CharSequence> tabletypeadapter = ArrayAdapter.createFromResource(AddCustomer.this,R.array.Tabletype,android.R.layout.simple_spinner_item);
        tabletypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        table_type.setAdapter(tabletypeadapter);

        noofcustomers.setMaxValue(10);
        noofcustomers.setMinValue(1);


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
                }
               else if(selected_table.equals("Bar Dining")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(12);
                }
                else if(selected_table.equals("VIP Dining")){
                    table_no.setMinValue(1);
                    table_no.setMaxValue(5);
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


    private void addDatatoFirebase() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        String tableno = "T"+table_no.getValue();
        String datetoday = format.format(date);
        String day = datetoday.substring(0,2).concat(datetoday.substring(3,5)).concat(datetoday.substring(6,8));
        String time = datetoday.substring(datetoday.length() - 8);
        String doc_table_no = day.concat(time).concat(tableno);
        Log.e("doctable",doc_table_no);

       CustomerInfo customerInfo = new CustomerInfo(table_no.getValue(),noofcustomers.getValue(),datetoday,false,true,selected_table);

       db.collection("Cust1").document(doc_table_no).set(customerInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Toast.makeText(AddCustomer.this, "Added", Toast.LENGTH_SHORT).show();
               finish();

           }

       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(AddCustomer.this, "Failed", Toast.LENGTH_SHORT).show();

           }
       });



    }

    public void generateKOT() {
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
    }
}
