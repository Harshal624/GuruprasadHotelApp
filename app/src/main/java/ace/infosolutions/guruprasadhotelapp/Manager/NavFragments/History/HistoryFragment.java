package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.History;

import android.content.Intent;
import android.os.Bundle;
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
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.HistoryModel;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.HISTORY;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historyfragment, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Customer history");
        recyclerView = view.findViewById(R.id.recyclerview_history);
        layoutManager = new LinearLayoutManager(getContext());
        historyRef = db.collection(HISTORY);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        adapter.setOnFinalBillItemTitleClickListener(new HistoryFirestoreAdapter.onFinalBillItemTitleClick() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int pos) {
                Intent intent = new Intent(getContext(), HistoryItems.class);
                intent.putExtra("itemhistorylist", snapshot.getId());
                startActivity(intent);
            }
        });

    }

    private void setupRecyclerView() {
        Query query = historyRef.orderBy("date_completed", Query.Direction.DESCENDING)//.orderBy(
                // "time_completed", Query.Direction.DESCENDING)
                ;
        FirestoreRecyclerOptions<HistoryModel> history = new FirestoreRecyclerOptions.Builder<HistoryModel>()
                .setQuery(query, HistoryModel.class)
                .build();
        adapter = new HistoryFirestoreAdapter(history, getView());
        recyclerView.setHasFixedSize(true);
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
