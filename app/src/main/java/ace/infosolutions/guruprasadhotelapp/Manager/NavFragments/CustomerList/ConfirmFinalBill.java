package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.R;

public class ConfirmFinalBill extends AppCompatActivity {
    private static final String FINAL_BILL = "FINAL_BILL";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ConfirmFinalBillFirestoreAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private String doc_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_bill);
        db= FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.final_bill_recycler);
        doc_id = getIntent().getStringExtra("FinalDOCID");
        layoutManager = new LinearLayoutManager(this);
        collectionReference  = db.collection("Customers");

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Query query = collectionReference.document(doc_id).collection(FINAL_BILL).whereEqualTo("isrequested",true)
                .whereEqualTo("isconfirmed",true);
        FirestoreRecyclerOptions<FinalBillClass> cust = new FirestoreRecyclerOptions.Builder<FinalBillClass>()
                .setQuery(query,FinalBillClass.class)
                .build();
        adapter = new ConfirmFinalBillFirestoreAdapter(cust);
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
