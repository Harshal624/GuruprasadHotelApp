package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Date;

import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

public class AddParcel extends AppCompatActivity {
    public static final String SP_KEY = "SP_KEY";
    public static final String PARCEL_ID_KEY = "PARCEL_ID_KEY";
    private static final String PARCELS = "PARCELS";
    private EditText cust_name, cust_contact, cust_address;
    private RadioGroup radioGroup;
    private Button confirm_parcel;
    private boolean ishomedelivery;
    private FirebaseFirestore db;
    private CollectionReference parcelRef;
    private ProgressBar progressBar;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);
        radioGroup = findViewById(R.id.radiogroup);
        cust_address = findViewById(R.id.cust_address);
        cust_name = findViewById(R.id.cust_name);
        cust_contact = findViewById(R.id.cust_contact);
        confirm_parcel = findViewById(R.id.confirm_parcel);
        progressBar = findViewById(R.id.progress_bar);
        db = FirebaseFirestore.getInstance();
        parcelRef = db.collection(PARCELS);
        preferences = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);
                String parcelType = radioButton.getText().toString();
                if (parcelType.equals("Parcel")) {
                    cust_address.setVisibility(View.GONE);
                    ishomedelivery = false;
                } else if (parcelType.equals("Home Delivery")) {
                    cust_address.setVisibility(View.VISIBLE);
                    ishomedelivery = true;
                }
            }
        });


        confirm_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customerName = cust_name.getText().toString().trim();
                String customerContact = cust_contact.getText().toString().trim();
                String customerAddress = cust_address.getText().toString().trim();

                if (customerName.equals("") && customerContact.equals("")) {
                    if (customerAddress.equals("") && ishomedelivery == true) {
                        cust_name.setError("Enter customer name");
                        cust_contact.setError("Enter contact");
                        cust_address.setError("Enter address");
                    } else {
                        cust_name.setError("Enter customer name");
                        cust_contact.setError("Enter contact");
                    }
                } else if (!customerName.equals("") && customerContact.equals("")) {
                    if (customerAddress.equals("") && ishomedelivery == true) {
                        cust_contact.setError("Enter contact");
                        cust_address.setError("Enter address");
                    } else {
                        cust_contact.setError("Enter contact");
                    }

                } else if (customerName.equals("") && !customerContact.equals("")) {
                    if (customerAddress.equals("") && ishomedelivery == true) {
                        cust_name.setError("Enter contact");
                        cust_address.setError("Enter address");
                    } else {
                        cust_name.setError("Enter contact");
                    }
                } else {
                    if (customerAddress.equals("") && ishomedelivery == true) {
                        cust_address.setError("Enter address");
                    } else {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                        Date date = new Date();
                        String datetoday = format.format(date);
                        String final_date = datetoday.replaceAll("/", "-");
                        ParcelModel model = new ParcelModel(customerName, customerContact, ishomedelivery, customerAddress, 0.0, 0.0, final_date);
                        confirmParcel(model);
                    }
                }

            }
        });
    }

    private void confirmParcel(ParcelModel model) {
        InternetConn conn = new InternetConn(this);
        if (conn.haveNetworkConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            WriteBatch batch = db.batch();
            final DocumentReference reference = parcelRef.document();
            batch.set(reference, model);

            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String parcel_id = reference.getId();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PARCEL_ID_KEY, parcel_id);
                    editor.commit();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddParcel.this, "Parcel Added", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), FoodMenuParcel.class));
                }
            });
        } else {
            Toast.makeText(AddParcel.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }
}
