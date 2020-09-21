package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class RequestedKOTFragment extends Fragment {
    private RecyclerView.LayoutManager layoutManager;
    private RequestedKOTFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private static final String CUSTOMER_COLLECTION = "Customers";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_requested_kot,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("Requested KOT's");
        recyclerView =(RecyclerView)view.findViewById(R.id.requestedkot_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(CUSTOMER_COLLECTION);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        adapter.setOnItemClickListener(new RequestedKOTFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String doc_id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), Confirm_Cancel_Order.class);
                intent.putExtra("DOCID",doc_id);
                startActivity(intent);
            }
        });


    }

    private void setupRecyclerView() {
        Query query = collectionReference.whereEqualTo("kotrequested", true);
        FirestoreRecyclerOptions<requestedkotmodel> cust =
                new FirestoreRecyclerOptions.Builder<requestedkotmodel>()
                        .setQuery(query, requestedkotmodel.class)
                        .build();
        adapter = new RequestedKOTFirestoreAdapter(cust,getView());
        recyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
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
