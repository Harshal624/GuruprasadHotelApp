package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__cancel__order);
        recyclerView = findViewById(R.id.confirm_cancelRecycler);
        layoutManager = new LinearLayoutManager(this);
        print_order = (Button) findViewById(R.id.print_kot);
        String doc_id = getIntent().getStringExtra("DOCID");
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
        //TODO Reset COST subcollection to zero
        //TODO Update parent document cost field with the subcollection field before resetting
        //TODO Update kotrequested field from parent document to false after printing
        //TODO Delete all KOT subcollection documents by iterating through all the documents

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
