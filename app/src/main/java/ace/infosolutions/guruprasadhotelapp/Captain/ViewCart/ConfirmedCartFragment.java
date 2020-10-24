package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.R;

public class ConfirmedCartFragment extends Fragment {
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String CUSTOMERS = "CUSTOMERS";
    public static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private ConfirmedCartCaptainAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference confirmedRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmedcart_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_confirmed);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY, "");
        confirmedRef = db.collection(CUSTOMERS).document(DOC_ID).collection(CONFIRMED_KOT);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }


    private void setupRecyclerView() {
        Query query = confirmedRef;
        FirestoreRecyclerOptions<ViewCartModel> viewcart =
                new FirestoreRecyclerOptions.Builder<ViewCartModel>()
                        .setQuery(query, ViewCartModel.class)
                        .build();
        adapter = new ConfirmedCartCaptainAdapter(viewcart, getView());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
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
