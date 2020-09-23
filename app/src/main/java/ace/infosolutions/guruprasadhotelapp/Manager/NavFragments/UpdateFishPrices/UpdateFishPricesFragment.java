package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFishPrices;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.internal.$Gson$Preconditions;

import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishModel;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class UpdateFishPricesFragment extends Fragment {
    private static final String FISH = "FISH";
    private static final String CUSTOMERS = "Customers";
    private static final String KOT = "KOT";
    private static final String FINAL_BILL = "FINAL_BILL";
    private static final String COST = "COST";
    private RecyclerView recyclerView;
    private FishFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference fishRef;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private EditText enter_cost;
    private View view1;
    String fish_doc_id;
    private InternetConn conn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_update_fish_prices,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("Update Fish Prices");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_updatefish);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        fishRef = db.collection(FISH);
        view1 = inflater.inflate(R.layout.updatefish_alertdialog,null);
        builder = new AlertDialog.Builder(getContext());
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        enter_cost = (EditText)view1.findViewById(R.id.fish_cost);
        conn = new InternetConn(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerview();

        setupAlertDialog();

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    enter_cost.setHint("Rs.");
                    enter_cost.setText(null);
                    FishModel fishModel = documentSnapshot.toObject(FishModel.class);
                    fish_doc_id = documentSnapshot.getId();
                    String fish_title = fishModel.getItem_title();
                    alertDialog.setTitle(fish_title);
                    if(conn.haveNetworkConnection())
                        alertDialog.show();
                    else
                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupAlertDialog() {
        alertDialog.setIcon(R.drawable.rupee);
        alertDialog.setView(view1);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fish_cost = enter_cost.getText().toString().trim();
                if(fish_cost.isEmpty())
                    Toast.makeText(getContext(), "No cost entered", Toast.LENGTH_SHORT).show();
                else{
                    double cost_fish = Double.parseDouble(fish_cost);
                    updateFishPrice(cost_fish,fish_doc_id);
                }
            }
        });
    }

    private void updateFishPrice(double cost_fish,String fish_doc_id) {
        fishRef.document(fish_doc_id).update("item_cost",cost_fish).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Failed to update the cost", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerview() {
        Query query = fishRef;
        FirestoreRecyclerOptions<FishModel> updateFishOptions = new
                FirestoreRecyclerOptions.Builder<FishModel>()
                .setQuery(query,FishModel.class)
                .build();
        adapter = new FishFirestoreAdapter(updateFishOptions);
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
