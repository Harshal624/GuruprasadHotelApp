package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters.MonthModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters.TableMonthListAdapter;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TABLETALLYMONTHLY;

public class TableTallyMonthList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TableMonthListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_tally_date_list);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        setUpRecyclerView();
        adapter.setOnMonthClickListener(new TableMonthListAdapter.OnMonthClickListener() {
            @Override
            public void OnMonthClick(DocumentSnapshot snapshot) {
                Intent intent = new Intent(getApplicationContext(), TableTally.class);
                intent.putExtra("DOCID", snapshot.getId());
                intent.putExtra("ISDAILY", false);
                startActivity(intent);
            }
        });

    }

    private void setUpRecyclerView() {
        Query query = db.collection(TABLETALLYMONTHLY);
        FirestoreRecyclerOptions<MonthModel> date_list = new FirestoreRecyclerOptions.Builder<MonthModel>()
                .setQuery(query, MonthModel.class)
                .build();
        adapter = new TableMonthListAdapter(date_list);
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
