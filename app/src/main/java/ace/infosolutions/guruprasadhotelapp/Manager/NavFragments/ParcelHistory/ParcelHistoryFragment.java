package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ParcelHistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ConfirmedCartParcelFragment.PARCEL_HISTORY;

public class ParcelHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ParcelHistoryFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference phistoryRef;
    private Query query;
    private ImageButton searchButton;
    private EditText searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parcel_history_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        phistoryRef = db.collection(PARCEL_HISTORY);
        ((Manager) getActivity()).toolbar.setTitle("Parcel History");
        searchBar = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchButton);
        query = phistoryRef.orderBy("date_completed", Query.Direction.DESCENDING).orderBy(
                "time_completed", Query.Direction.DESCENDING
        );
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = searchBar.getText().toString().trim();
                if (date.equals("") || date.equals(null)) {
                    Toast.makeText(getContext(), "Enter a date first!", Toast.LENGTH_SHORT).show();
                } else if (date.length() != 8) {
                    Toast.makeText(getContext(), "Wrong format, Please check the format and try again", Toast.LENGTH_SHORT).show();
                } else {
                    String d1 = date.substring(2, 3);
                    String d2 = date.substring(5, 6);
                    if (d1.equals("-") && d2.equals("-")) {
                        query = phistoryRef.whereEqualTo("date_completed", date).orderBy("date_completed", Query.Direction.DESCENDING).orderBy(
                                "time_completed", Query.Direction.DESCENDING
                        );
                        setupRecyclerView();
                        adapter.startListening();
                    } else {
                        Toast.makeText(getContext(), "Wrong format, Please check the format and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
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
