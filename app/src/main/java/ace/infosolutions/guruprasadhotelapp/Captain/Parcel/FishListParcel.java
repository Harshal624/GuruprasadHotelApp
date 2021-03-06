package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FoodMenuModel;
import ace.infosolutions.guruprasadhotelapp.Captain.ItemAlertDialog;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ViewCartParcel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;


public class FishListParcel extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
    private RecyclerView recyclerView;
    private FishFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference fishRef, currentRef, parcelRef;
    private SharedPreferences sharedPreferences;

    private String doc_id;
    private FloatingActionButton check_cart;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_list);
        check_cart = (FloatingActionButton) findViewById(R.id.check_cart);
        db = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressbar_fish);
        fishRef = db.collection(Constants.FISH);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_fishmain);
        layoutManager = new LinearLayoutManager(this);
        sharedPreferences = getSharedPreferences(Constants.SP_KEY, Context.MODE_PRIVATE);
        doc_id = sharedPreferences.getString(Constants.PARCEL_ID_KEY, "");
        currentRef = db.collection(Constants.PARCELS).document(doc_id).collection(Constants.CURRENT_KOT);
        parcelRef = db.collection(Constants.PARCELS);

        setUpRecyclerView();
        hideViewCartButton();

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FoodMenuModel model = documentSnapshot.toObject(FoodMenuModel.class);
                String item_title = model.getItem_title();
                String item_cost = "Rs." + model.getItem_cost();
                String item_title_english = model.getItem_title_english();
                openDialog(item_title, item_cost, item_title_english);
            }
        });
        check_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCartParcel.class));
            }
        });

    }

    private void openDialog(String item_title, String item_cost, String item_title_english) {
        ItemAlertDialog itemAlertDialog = new ItemAlertDialog(item_title, item_cost, item_title_english);
        itemAlertDialog.show(getSupportFragmentManager(), "dialog");
    }

    private void setUpRecyclerView() {
        Query query = fishRef;
        FirestoreRecyclerOptions<FoodMenuModel> fishOptions = new FirestoreRecyclerOptions.Builder<FoodMenuModel>()
                .setQuery(query, FoodMenuModel.class)
                .build();
        adapter = new FishFirestoreAdapter(fishOptions);
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

    @Override
    public void applyText(final String item_title, final double item_cost, final int qty, final String item_title_english) {
        check_cart.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        InternetConn conn = new InternetConn(this);
        final FoodItemModel model = new FoodItemModel(item_title, item_cost, qty, item_title_english);
        final DocumentReference currRef = currentRef.document();
        final DocumentReference parcRef = parcelRef.document(doc_id);
        if (conn.haveNetworkConnection()) {
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(parcRef);
                    double current_cost = snapshot.getDouble("current_cost");
                    double final_cost = item_cost + current_cost;
                    transaction.set(currRef, model);
                    transaction.update(parcRef, "current_cost", final_cost);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility(View.GONE);
                    check_cart.setEnabled(true);
                    Toast.makeText(FishListParcel.this, "Added!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            check_cart.setEnabled(true);
        }
    }

    private void hideViewCartButton() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && check_cart.isShown()) {
                    check_cart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    check_cart.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

}
