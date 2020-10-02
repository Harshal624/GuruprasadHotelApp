package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.Collection;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.TableTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.Adapters.CalculateTableTallyAdapter;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TABLETALLYDAILY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TABLETALLYMONTHLY;

public class CalculateTallyTable extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView table_tally_type;
    String toolbar_title;
    private RecyclerView.LayoutManager layoutManager;
    private CalculateTableTallyAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Query query;
    private CollectionReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tally_table);
        table_tally_type = findViewById(R.id.table_tally_type);
        recyclerView = findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(this);

        boolean isdaily = getIntent().getBooleanExtra("ISDAILY",false);
        String doc_id = getIntent().getStringExtra("DOCID");
        String table_type = getIntent().getStringExtra("TYPE");

        if(isdaily){
            toolbar_title = table_type+"(Daily)";
            ref = db.collection(TABLETALLYDAILY);
        }
        else{
            toolbar_title = table_type+"(Monthly)";
            ref = db.collection(TABLETALLYMONTHLY);
        }
        table_tally_type.setText(toolbar_title);


        query = ref.document(doc_id).collection(table_type);;

        setupRecyclerview();
    }

    private void setupRecyclerview() {
        FirestoreRecyclerOptions<TableTotalModel> tale_tally = new FirestoreRecyclerOptions.Builder<TableTotalModel>()
                .setQuery(query,TableTotalModel.class)
                .build();
        View view = findViewById(android.R.id.content).getRootView();
        adapter = new CalculateTableTallyAdapter(tale_tally,view);
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
