package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.History;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBillFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.CurrentCartFragment.CONFIRMED_KOT;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.HISTORY;

public class HistoryItems extends AppCompatActivity {
    private String DOC_ID;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private ConfirmFinalBillFirestoreAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_items);
        recyclerView = findViewById(R.id.recyclerview);
        DOC_ID = getIntent().getStringExtra("itemhistorylist");
        layoutManager = new LinearLayoutManager(this);
        db = FirebaseFirestore.getInstance();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = db.collection(HISTORY).document(DOC_ID).collection(CONFIRMED_KOT);
        FirestoreRecyclerOptions<FinalBillModel> viewcart = new FirestoreRecyclerOptions.Builder<FinalBillModel>()
                .setQuery(query, FinalBillModel.class)
                .build();
        adapter = new ConfirmFinalBillFirestoreAdapter(viewcart);
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
