package ace.infosolutions.guruprasadhotelapp.Captain;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class Captain extends AppCompatActivity {
    private RecyclerView customerListRecycler;
    private RecyclerView.Adapter customerListAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain);
        customerListRecycler = findViewById(R.id.customerRecyclerView);

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




    }
}
