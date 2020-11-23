package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ParcelHistory;

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

import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelHistoryModel;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;


public class ParcelHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ParcelHistoryFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference phistoryRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parcel_history_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        phistoryRef = db.collection(Constants.PARCEL_HISTORY);
        ((Manager) getActivity()).toolbar.setTitle("Parcel History");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        adapter.setOnFinalBillItemTitleClickListener(new ParcelHistoryFirestoreAdapter.onFinalBillItemTitleClick() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int pos) {
                Intent intent = new Intent(getContext(), ParcelHistoryItemList.class);
                intent.putExtra("PARCELHISTORYITEMS", snapshot.getId());
                startActivity(intent);
            }
        });


    }

    private void setupRecyclerView() {
        Query query = phistoryRef.orderBy("date_completed", Query.Direction.DESCENDING).orderBy(
                "time_completed", Query.Direction.DESCENDING)
                ;
        FirestoreRecyclerOptions<ParcelHistoryModel> phistory = new FirestoreRecyclerOptions.Builder<ParcelHistoryModel>()
                .setQuery(query, ParcelHistoryModel.class)
                .build();
        adapter = new ParcelHistoryFirestoreAdapter(phistory);
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
