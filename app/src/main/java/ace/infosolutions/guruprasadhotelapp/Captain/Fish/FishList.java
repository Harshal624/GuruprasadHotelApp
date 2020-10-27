package ace.infosolutions.guruprasadhotelapp.Captain.Fish;

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
import ace.infosolutions.guruprasadhotelapp.Captain.ItemAlertDialog;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCart;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.CURRENT_KOT;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.CUSTOMERS;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.FISH;

public class FishList extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {

    private RecyclerView recyclerView;
    private FishFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference fishRef, currentRef, custRef;
    private SharedPreferences sharedPreferences;

    private String doc_id;
    private FloatingActionButton check_cart;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_list);
        check_cart = findViewById(R.id.check_cart);
        progressBar = findViewById(R.id.progressbar_fish);
        fishRef = db.collection(FISH);
        recyclerView = findViewById(R.id.recycler_fishmain);
        layoutManager = new LinearLayoutManager(this);
        sharedPreferences = getSharedPreferences(Constants.PREF_DOCID, Context.MODE_PRIVATE);
        doc_id = sharedPreferences.getString(Constants.DOC_ID_KEY, "");
        currentRef = db.collection(CUSTOMERS).document(doc_id).collection(CURRENT_KOT);
        custRef = db.collection(CUSTOMERS);

        setUpRecyclerView();
        hideViewCartButton();

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FishModel model = documentSnapshot.toObject(FishModel.class);
                String item_title = model.getItem_title();
                String item_cost = "Rs." + model.getItem_cost();
                openDialog(item_title, item_cost);
            }
        });
        check_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCart.class));
            }
        });

    }

    private void openDialog(String item_title, String item_cost) {
        ItemAlertDialog itemAlertDialog = new ItemAlertDialog(item_title, item_cost);
        itemAlertDialog.show(getSupportFragmentManager(), "dialog");
    }

    private void setUpRecyclerView() {
        Query query = fishRef;
        FirestoreRecyclerOptions<FishModel> fishOptions = new FirestoreRecyclerOptions.Builder<FishModel>()
                .setQuery(query, FishModel.class)
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
    public void applyText(final String item_title, final double item_cost, final int qty) {
        check_cart.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        FoodItemModel model = new FoodItemModel(item_title, item_cost, qty);
        addItemToDB(model, item_cost);
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

    private void addItemToDB(final FoodItemModel model, final double item_cost) {
        final DocumentReference costRef = db.collection(CUSTOMERS).document(doc_id);
        final DocumentReference addItemRef = db.collection(CUSTOMERS).document(doc_id).collection(CURRENT_KOT).document();
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(costRef);
                double cost = snapshot.getDouble("current_cost");
                double total_cost = cost + item_cost;
                transaction.update(costRef, "current_cost", total_cost);
                transaction.set(addItemRef, model);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                check_cart.setEnabled(true);
                Toast.makeText(FishList.this, "Added", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
