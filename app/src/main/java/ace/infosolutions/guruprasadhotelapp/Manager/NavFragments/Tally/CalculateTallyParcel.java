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

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.ParcelTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters.ParcelTotalAdapter;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.DAILY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.MONTHLY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TALLY;

public class CalculateTallyParcel extends AppCompatActivity {
    public static final String PARCELS = "PARCELS";
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ParcelTotalAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String tally_type;
    private TextView t_type;
    private ImageView t_type_image;
    private Query query;
    private FirestoreRecyclerOptions<ParcelTotalModel> tally;
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
        computeTallyType();
        setUpRecyclerView();

        final ImageButton sort = findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coll;
                if (tally_type.equals("DAILYPARCEL")) {
                    coll = DAILY;
                } else {
                    coll = MONTHLY;
                }
                if (ascending) {
                    Toast.makeText(CalculateTallyParcel.this, "Sorted->Revenue high to low", Toast.LENGTH_SHORT).show();
                    ascending = false;
                    sort.setEnabled(false);
                    query = db.collection(TALLY).document(coll).collection(PARCELS).orderBy("parceltotal", Query.Direction.DESCENDING);
                    setUpRecyclerView();
                    adapter.startListening();
                    sort.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sort.setEnabled(true);
                        }
                    }, 2500);

                } else {
                    Toast.makeText(CalculateTallyParcel.this, "Sorted->Revenue low to high", Toast.LENGTH_SHORT).show();
                    ascending = true;
                    sort.setEnabled(false);
                    query = db.collection(TALLY).document(coll).collection(PARCELS).orderBy("parceltotal", Query.Direction.ASCENDING);
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
        });
    }

    private void computeTallyType() {
        switch (tally_type) {
            case "DAILYPARCEL":
                query = db.collection(TALLY).document(DAILY).collection(PARCELS);
                t_type.setText("Parcel Grandtotal");
                t_type_image.setImageResource(R.drawable.parcel);
                break;
            case "MONTHLYPARCEL":
                query = db.collection(TALLY).document(MONTHLY).collection(PARCELS);
                t_type.setText("Monthly Parcel");
                t_type_image.setImageResource(R.drawable.parcel);
                break;

        }

    }

    private void setUpRecyclerView() {
        tally = new FirestoreRecyclerOptions.Builder<ParcelTotalModel>()
                .setQuery(query, ParcelTotalModel.class)
                .build();
        adapter = new ParcelTotalAdapter(tally);
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
