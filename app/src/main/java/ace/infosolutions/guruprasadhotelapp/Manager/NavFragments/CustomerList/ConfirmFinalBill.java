package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

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
    private TextView tableNo,tableType,totalCost;
    private Button printBill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_bill);
        db= FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.final_bill_recycler);
        doc_id = getIntent().getStringExtra("FinalDOCID");
        layoutManager = new LinearLayoutManager(this);
        collectionReference  = db.collection("Customers");

        printBill = (Button) findViewById(R.id.final_billPrint);
        tableNo = (TextView)findViewById(R.id.final_billtableno);
        tableType = (TextView)findViewById(R.id.final_billtabletype);
        totalCost = (TextView)findViewById(R.id.final_billTotalCost);

        fetchTableInfoTotalcost();
        setupRecyclerView();
    }

    private void fetchTableInfoTotalcost() {
        collectionReference.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String table_no = String.valueOf(documentSnapshot.getLong("table_no").intValue());
                String total_cost = String.valueOf(documentSnapshot.getDouble("cost"));
                String table_type = documentSnapshot.getString("table_type");
                tableNo.setText(table_no);
                tableType.setText(table_type);
                totalCost.setText(total_cost);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tableNo.setText("Error!");
                tableType.setText("Error!");
                totalCost.setText("Error!");

            }
        });
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
