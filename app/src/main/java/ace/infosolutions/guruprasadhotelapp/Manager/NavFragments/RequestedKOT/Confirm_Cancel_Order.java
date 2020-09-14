package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartPOJO;
import ace.infosolutions.guruprasadhotelapp.R;

public class Confirm_Cancel_Order extends AppCompatActivity {
    private ViewCartFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private Button delete_order,print_order;
    private static final String CUSTOMERS="Customers";
    private static final String KOT="KOT";
    private static final String COST="COST";
    String doc_id;
    Map<String,Object> update_costmap;
    Map<String,Object> reset_costcoll;
    Map<String,Object> update_kotrequestedmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__cancel__order);
        recyclerView = findViewById(R.id.confirm_cancelRecycler);
        layoutManager = new LinearLayoutManager(this);
        print_order = (Button) findViewById(R.id.print_kot);
        doc_id = getIntent().getStringExtra("DOCID");
        //
        update_costmap = new HashMap<>();
        reset_costcoll = new HashMap<>();
        reset_costcoll.put("cost",0);
        update_kotrequestedmap = new HashMap<>();
        update_kotrequestedmap.put("kotrequested",false);
        //
        collectionReference = db.collection("Customers").document(doc_id).collection("KOT");
        setupRecyclerView();

        print_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print_kot();
            }
        });
    }

    private void print_kot() {
        //3.TODO Reset COST subcollection to zero
        //2.TODO Update parent document cost field with the subcollection field before resetting
        //4.TODO Update kotrequested field from parent document to false after printing
        //1.TODO Delete all KOT subcollection documents by iterating through all the documents
        db.collection(CUSTOMERS).document(doc_id).collection(KOT)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String DOC_ID = snapshot.getId();
                        db.collection(CUSTOMERS).document(doc_id).collection(KOT).document(DOC_ID)
                                .delete();
                    }
                    updateParentCostField();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Failed to confirm the oreder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateParentCostField() {
        db.collection(CUSTOMERS).document(doc_id).collection(COST).document(COST)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = snapshot.getDouble("cost");
                    if(cost!=0){
                        update_costmap.put("cost",cost);
                        db.collection(CUSTOMERS).document(doc_id)
                                .update(update_costmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    resetCostsubcollection();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Confirm_Cancel_Order.this, "Cannot confirm the order", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void resetCostsubcollection() {
        db.collection(CUSTOMERS).document(doc_id).collection(COST).document(COST)
                .update(reset_costcoll).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateparent_kotreq();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateparent_kotreq() {
        db.collection(CUSTOMERS).document(doc_id).update(update_kotrequestedmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Confirm_Cancel_Order.this, "KOT Confirmed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Cannot confirm the order!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        Query query = collectionReference.whereEqualTo("isrequested",true);
        FirestoreRecyclerOptions<ViewCartPOJO> cust = new FirestoreRecyclerOptions.Builder<ViewCartPOJO>()
                .setQuery(query,ViewCartPOJO.class)
                .build();
        adapter = new ViewCartFirestoreAdapter(cust);
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
