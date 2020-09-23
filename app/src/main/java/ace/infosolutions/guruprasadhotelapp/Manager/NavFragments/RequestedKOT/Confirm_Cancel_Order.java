package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private static final String REQUESTED_KOT = "REQUESTED_KOT";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private ViewCartFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference collectionReference,custRef,collectionRefConfirmed;
    private Button print_order;
    private static final String CUSTOMERS="CUSTOMERS";
    String doc_id;
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
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(CUSTOMERS).document(doc_id).collection(REQUESTED_KOT);
        collectionRefConfirmed = db.collection(CUSTOMERS).document(doc_id).collection(CONFIRMED_KOT);
        custRef = db.collection(CUSTOMERS);
        setupRecyclerView();

        print_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conn.haveNetworkConnection()){
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot snapshot:task.getResult()){
                                    ViewCartPOJO model = snapshot.toObject(ViewCartPOJO.class);
                                    collectionRefConfirmed.document().set(model);
                                }
                                updateReqCostField();
                            }
                        }
                    });



                }
                else{
                    Toast.makeText(Confirm_Cancel_Order.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateReqCostField() {
        //adding confirmed and requested costs
        custRef.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String,Object> finalcost = new HashMap<>();
                    final Map<String,Object> reqcostzero = new HashMap<>();
                    reqcostzero.put("requested_cost",0);
                    double requested_cost = task.getResult().getDouble("requested_cost");
                    double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                    double final_cost = requested_cost + confirmed_cost;
                    finalcost.put("confirmed_cost",final_cost);
                    custRef.document(doc_id).update(finalcost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            //setting requested cost to zero
                            custRef.document(doc_id).update(reqcostzero).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           deleteRequCostColl();
                                       }
                                }

                            });
                        }
                        }
                    });
                }
            }
        });
    }

    private void deleteRequCostColl() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String reqkot_id = snapshot.getId();
                        collectionReference.document(reqkot_id).delete();
                    }
                }
                updateKOTRequested();
            }
        });
    }

    private void updateKOTRequested() {
        custRef.document(doc_id).update("kotrequested",false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Confirm_Cancel_Order.this, "Order Confirmed, printing KOT", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(),Manager.class));
                    overridePendingTransition(0,0);
                }

            }
        });
    }


    private void setupRecyclerView() {
        Query query = collectionReference;
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
