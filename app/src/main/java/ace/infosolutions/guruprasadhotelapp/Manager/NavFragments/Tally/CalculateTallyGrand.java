package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
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
    private CollectionReference tallyRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GrandTotalAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String tally_type;
    private TextView t_type;
    private ImageView t_type_image;
    private Query query;
    private FirestoreRecyclerOptions<GrandTotalModel> tally;

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
    }

    private void computeTallyType() {
        switch (tally_type){
            case "DAILYGRAND":tallyRef = db.collection(TALLY).document(DAILY).collection(GRANDTOTAL);
                t_type.setText("Daily Grandtotal");
                t_type_image.setImageResource(R.drawable.daily);
                break;
            case "MONTHLYGRAND": tallyRef = db.collection(TALLY).document(MONTHLY).collection(GRANDTOTAL);
                t_type.setText("Monthly Grandtotal");
                t_type_image.setImageResource(R.drawable.monthly);
                break;

        }

    }

    private void setUpRecyclerView() {
        query = tallyRef;//.orderBy("grandtotal", Query.Direction.DESCENDING);
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
