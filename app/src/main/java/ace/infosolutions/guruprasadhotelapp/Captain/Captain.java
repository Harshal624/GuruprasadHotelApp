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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class Captain extends AppCompatActivity {
    private RecyclerView customerListRecycler;
    private RecyclerView.Adapter customerListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton add_customer;
    private  AlertDialog alertDialog1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain);
        customerListRecycler = findViewById(R.id.customerRecyclerView);
        add_customer = (FloatingActionButton)findViewById(R.id.add_customer);
        setupAlertdialog();
        //dummy entries
        ArrayList<customerclass> customerclasses = new ArrayList<>();
        customerclasses.add(new customerclass(4,43.65));
        customerclasses.add(new customerclass(6,453.54));
        customerclasses.add(new customerclass(8,324.654));
        customerclasses.add(new customerclass(1,453.54));
        customerclasses.add(new customerclass(33,453.54));
        customerclasses.add(new customerclass(23,453.54));
        customerclasses.add(new customerclass(21,453.54));
        customerclasses.add(new customerclass(16,453.54));

        //setting up recyclerview
        customerListRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        customerListAdapter = new CustomerAdapter(customerclasses);
        customerListRecycler.setLayoutManager(layoutManager);
        customerListRecycler.setAdapter(customerListAdapter);


        //add customer
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(getApplicationContext(),SelectTable.class));
                alertDialog1.show();
            }
        });

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
                       // Toast.makeText(Captain.this, "Orders", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Captain.this, "Table parcel", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        alertDialog1 = alertDialog.create();
    }
}
