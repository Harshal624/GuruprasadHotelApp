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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.R;

public class RequestedKOTFragmentCaptain extends Fragment {
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String REQUESTED_KOT = "REQUESTED_KOT";
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private RequestedKOTFirestoreAdapterCaptain adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private TextView total_price_requested;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requestedkotfragment,container,false);
        recyclerView = view.findViewById(R.id.requested_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        total_price_requested = view.findViewById(R.id.total_costReqKOT);
        sharedPreferences = getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY, "");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchTotalRequestedKOTPrice();
        setupRecyclerview();

    }

    private void fetchTotalRequestedKOTPrice() {
        db.collection(CUSTOMERS).document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    double total_cost = task.getResult().getDouble("requested_cost");
                    String cost = String.valueOf(total_cost);
                    total_price_requested.setText("Total Cost: Rs."+cost);
                }
            }
        });
    }

    private void setupRecyclerview() {
        Query query = db.collection(CUSTOMERS).document(DOC_ID).collection(REQUESTED_KOT);
        FirestoreRecyclerOptions<ViewCartPOJO> viewcart =
                new FirestoreRecyclerOptions.Builder<ViewCartPOJO>()
                .setQuery(query,ViewCartPOJO.class)
                .build();
        adapter = new RequestedKOTFirestoreAdapterCaptain(viewcart);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
