package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.GrandTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters.GrandTotalAdapter;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.DAILY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.GRANDTOTAL;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.MONTHLY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TALLY;

public class CalculateTallyGrand extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GrandTotalAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String tally_type;
    private TextView t_type;
    private ImageView t_type_image;
    private Query query;
    private FirestoreRecyclerOptions<GrandTotalModel> tally;
    private ImageButton sort;
    private boolean ascending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tally);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        t_type = findViewById(R.id.tally_type);
        t_type_image = findViewById(R.id.tally_type_image);
        tally_type = getIntent().getStringExtra("TALLYTYPE");
        sort = findViewById(R.id.sort);
        computeTallyType();
        setUpRecyclerView();

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tally_type.equals("DAILYGRAND")) {
                    if (ascending) {
                        Toast.makeText(CalculateTallyGrand.this, "Sorted->Revenue high to low", Toast.LENGTH_SHORT).show();
                        ascending = false;
                        sort.setEnabled(false);
                        query = db.collection(TALLY).document(DAILY).collection(GRANDTOTAL).orderBy("grandtotal", Query.Direction.DESCENDING);
                        setUpRecyclerView();
                        adapter.startListening();
                        sort.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sort.setEnabled(true);
                            }
                        }, 2500);

                    } else {
                        Toast.makeText(CalculateTallyGrand.this, "Sorted->Revenue low to high", Toast.LENGTH_SHORT).show();
                        ascending = true;
                        sort.setEnabled(false);
                        query = db.collection(TALLY).document(DAILY).collection(GRANDTOTAL).orderBy("grandtotal", Query.Direction.ASCENDING);
                        setUpRecyclerView();
                        adapter.startListening();
                        sort.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sort.setEnabled(true);
                            }
                        }, 2500);
                    }
                } else if (tally_type.equals("MONTHLYGRAND")) {

                    if (ascending) {
                        Toast.makeText(CalculateTallyGrand.this, "Sorted->Revenue high to low", Toast.LENGTH_SHORT).show();
                        ascending = false;
                        sort.setEnabled(false);
                        query = db.collection(TALLY).document(MONTHLY).collection(GRANDTOTAL).orderBy("grandtotal", Query.Direction.DESCENDING);
                        setUpRecyclerView();
                        adapter.startListening();
                        sort.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sort.setEnabled(true);
                            }
                        }, 2500);

                    } else {
                        Toast.makeText(CalculateTallyGrand.this, "Sorted->Revenue low to high", Toast.LENGTH_SHORT).show();
                        ascending = true;
                        sort.setEnabled(false);
                        query = db.collection(TALLY).document(MONTHLY).collection(GRANDTOTAL).orderBy("grandtotal", Query.Direction.ASCENDING);
                        setUpRecyclerView();
                        adapter.startListening();
                        sort.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sort.setEnabled(true);
                            }
                        }, 2500);
                    }

                }


            }
        });
    }

    private void computeTallyType() {
        switch (tally_type){
            case "DAILYGRAND":
                query = db.collection(TALLY).document(DAILY).collection(GRANDTOTAL);
                t_type.setText("Daily Grandtotal");
                t_type_image.setImageResource(R.drawable.daily);
                break;
            case "MONTHLYGRAND":
                query = db.collection(TALLY).document(MONTHLY).collection(GRANDTOTAL);
                t_type.setText("Monthly Grandtotal");
                t_type_image.setImageResource(R.drawable.monthly);
                break;

        }

    }

    private void setUpRecyclerView() {
        tally = new FirestoreRecyclerOptions.Builder<GrandTotalModel>()
                .setQuery(query,GrandTotalModel.class)
                .build();
        adapter = new GrandTotalAdapter(tally);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
