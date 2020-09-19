package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import ace.infosolutions.guruprasadhotelapp.Captain.ItemList;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartPOJO;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class Confirm_Cancel_Order extends AppCompatActivity {
    private static final String FINAL_BILL = "FINAL_BILL";
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
    Map<String,Object> confirm_itemmap;
    private InternetConn conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__cancel__order);
        recyclerView = findViewById(R.id.confirm_cancelRecycler);
        layoutManager = new LinearLayoutManager(this);
        print_order = (Button) findViewById(R.id.print_kot);
        doc_id = getIntent().getStringExtra("DOCID");
        conn = new InternetConn(this);
        //
        update_costmap = new HashMap<>();
        reset_costcoll = new HashMap<>();
        confirm_itemmap = new HashMap<>();
        reset_costcoll.put("cost",0);
        update_kotrequestedmap = new HashMap<>();
        update_kotrequestedmap.put("kotrequested",false);
        confirm_itemmap.put("isconfirmed",true);
        //
        collectionReference = db.collection("Customers").document(doc_id).collection("KOT");
        setupRecyclerView();

        print_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conn.haveNetworkConnection()){
                    print_kot();
                }
                else{
                    Toast.makeText(Confirm_Cancel_Order.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

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
                        getParentDocCostandAdd(cost);
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

    private void getParentDocCostandAdd(final double cost) {
        db.collection(CUSTOMERS).document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double total_cost_doc = snapshot.getDouble("cost");
                    double final_cost = cost + total_cost_doc;
                    update_costmap.put("cost",final_cost);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
                    updateFinalBillisconfirmed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Cannot confirm the order!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFinalBillisconfirmed() {
        db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).whereEqualTo("isrequested",true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String finalDocId = snapshot.getId();
                        db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).document(finalDocId)
                                .update(confirm_itemmap);
                       }

                    Toast.makeText(Confirm_Cancel_Order.this, "KOT Generated", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), Manager.class));
                    overridePendingTransition(0,0);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm_Cancel_Order.this, "Failed!", Toast.LENGTH_SHORT).show();
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
