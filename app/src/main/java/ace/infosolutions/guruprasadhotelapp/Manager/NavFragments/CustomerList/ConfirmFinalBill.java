package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class ConfirmFinalBill extends AppCompatActivity {
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String TABLES = "Tables";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ConfirmFinalBillFirestoreAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference confirmedRef,custRef;
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
        confirmedRef = db.collection(CUSTOMERS).document(doc_id).collection(CONFIRMED_KOT);
        custRef = db.collection(CUSTOMERS);

        printBill = (Button) findViewById(R.id.final_billPrint);
        tableNo = (TextView)findViewById(R.id.final_billtableno);
        tableType = (TextView)findViewById(R.id.final_billtabletype);
        totalCost = (TextView)findViewById(R.id.final_billTotalCost);

        fetchTableInfoTotalcost();
        setupRecyclerView();
        printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO STORE THE FINAL BILL DOCUMENT DATA INCLUDING COST
                //TODO DELETE ALL THE SUBCOLLECTIONS
                //TODO DELETE PARENT DOCUMENT
                //TODO STORE CUSTOMER HISTORY
                //TODO SET TABLE FREE
                //TODO INCOMPLETE SAVETO HISTORY METHOD
               // savetoHistory();
            }
        });
    }

    private void setupRecyclerView() {
        Query query = confirmedRef;
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
    private void fetchTableInfoTotalcost() {
        custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String table_no = String.valueOf(documentSnapshot.getLong("table_no").intValue());
                String total_cost = String.valueOf(documentSnapshot.getDouble("confirmed_cost"));
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

}

