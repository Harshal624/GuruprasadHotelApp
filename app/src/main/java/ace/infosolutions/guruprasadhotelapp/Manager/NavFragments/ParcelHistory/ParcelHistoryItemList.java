package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ParcelHistory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBillFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ConfirmedCartParcelFragment.PARCEL_HISTORY;

public class ParcelHistoryItemList extends AppCompatActivity {
    private String DOC_ID;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ;
    private ConfirmFinalBillFirestoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_history_item_list);

        recyclerView = findViewById(R.id.recyclerview);
        DOC_ID = getIntent().getStringExtra("PARCELHISTORYITEMS");
        layoutManager = new LinearLayoutManager(this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        Query query = db.collection(PARCEL_HISTORY).document(DOC_ID).collection(Constants.CONFIRMED_KOT);
        FirestoreRecyclerOptions<FinalBillModel> viewcart = new FirestoreRecyclerOptions.Builder<FinalBillModel>()
                .setQuery(query, FinalBillModel.class)
                .build();
        adapter = new ConfirmFinalBillFirestoreAdapter(viewcart);
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
