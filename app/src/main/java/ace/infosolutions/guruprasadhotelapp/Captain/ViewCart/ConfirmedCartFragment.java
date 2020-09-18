package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.internal.$Gson$Preconditions;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBillFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.FinalBillClass;
import ace.infosolutions.guruprasadhotelapp.R;

public class ConfirmedCartFragment extends Fragment {
    private static final String CUSTOMERS = "Customers";
    private static final String FINAL_BILL = "FINAL_BILL";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ConfirmFinalBillFirestoreAdapter adapter;
    private FirebaseFirestore db;
    private String doc_id;
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private SharedPreferences sharedPreferences;
    private TextView total_cost_tv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmedcart_fragment,container,false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_confirmed);
        sharedPreferences= getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        layoutManager = new LinearLayoutManager(getContext());
        doc_id = sharedPreferences.getString(DOC_ID_KEY,"");
        total_cost_tv = (TextView)view.findViewById(R.id.total_cost_confirmed);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calculateTotalCost();
        setupRecyclerView();
    }

    private void calculateTotalCost() {
        db.collection(CUSTOMERS).document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int cost_value = documentSnapshot.getLong("cost").intValue();
                String cost = String.valueOf(cost_value);
                if(cost_value!=0)
                    total_cost_tv.setText("Total Cost:Rs. "+cost);
                else
                    total_cost_tv.setText("There are no confirmed orders!");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                total_cost_tv.setText("No orders");
            }
        });
    }

    private void setupRecyclerView() {
        Query query = db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).whereEqualTo("isconfirmed",true);
        FirestoreRecyclerOptions<FinalBillClass> final_bill = new FirestoreRecyclerOptions.Builder<FinalBillClass>()
                .setQuery(query,FinalBillClass.class)
                .build();
        adapter = new ConfirmFinalBillFirestoreAdapter(final_bill);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
