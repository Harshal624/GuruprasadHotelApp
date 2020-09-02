package ace.infosolutions.guruprasadhotelapp.Captain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.R;

public class Captain extends AppCompatActivity {

    private FloatingActionButton add_customer;
    private  AlertDialog alertDialog1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Customers");
    private CustomerFirestoreAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain);
        add_customer = (FloatingActionButton)findViewById(R.id.add_customer);
        setupAlertdialog();
        setupReyclerview();

        //add customer
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(getApplicationContext(),SelectTable.class));
                alertDialog1.show();
            }
        });
        adapter.setOnItemClickListener(new CustomerFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                customerclass customerclass = documentSnapshot.toObject(customerclass.class);
                String id = documentSnapshot.getId();
                Intent i = new Intent(getApplicationContext(),AddFood.class);
                i.putExtra("ID",id);
                i.putExtra("TableType",customerclass.getTable_type());
                i.putExtra("Table_no",String.valueOf(customerclass.getTable_no()));
                startActivity(i);
            }
        });
    }



    private void setupReyclerview() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query,customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapter(cust);
        RecyclerView recyclerView = findViewById(R.id.customerRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    public void setupAlertdialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Choose order type");
        String orders[] = {"Order","Table Parcel"};
        alertDialog.setItems(orders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(),AddCustomer.class));
                        break;
                    case 1:
                        Toast.makeText(Captain.this, "Parcel", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        alertDialog1 = alertDialog.create();
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
