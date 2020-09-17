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
    private static final String FINAL_BILL = "FINAL_BILL";
    private static final String CUSTOMERS = "Customers";
    private static final String KOT = "KOT";
    private static final String COST = "COST";
    private static final String TABLES = "Tables";
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
        printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO STORE THE FINAL BILL DOCUMENT DATA INCLUDING COST
                //TODO DELETE ALL THE SUBCOLLECTIONS
                //TODO DELETE PARENT DOCUMENT
                //TODO STORE CUSTOMER HISTORY
                //TODO SET TABLE FREE
                confirm_bill();
            }
        });
    }

    private void confirm_bill() {
        db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String DOCID = snapshot.getId();
                        db.collection("Customers").document(doc_id).collection("FINAL_BILL").document(DOCID).delete();
                    }
                    deleteKOTsub();
                    deleteCostsub();
                }

            }
        });
    }

    private void deleteCostsub() {
        db.collection(CUSTOMERS).document(doc_id).collection(COST).document(COST).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    updatetableColl();
            }
        });
    }

    private void deleteParentdoc() {
        db.collection(CUSTOMERS).document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ConfirmFinalBill.this, "Final bill generated", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), Manager.class));
                    overridePendingTransition(0,0);
                }

            }
        });
    }

    private void updatetableColl() {
        db.collection(CUSTOMERS).document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String table_type = task.getResult().getString("table_type");
                    String table_no = String.valueOf(task.getResult().getLong("table_no").intValue());
                    db.collection(TABLES).document(table_type).update(table_no,true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                deleteParentdoc();
                            }
                        }
                    });
                }
            }
        });


    }

    private void deleteKOTsub() {
        db.collection(CUSTOMERS).document(doc_id).collection(KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot: task.getResult()){
                        String DOCID= snapshot.getId();
                        db.collection(CUSTOMERS).document(doc_id).collection(KOT).document(DOCID).delete();
                    }
                }
            }
        });
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
