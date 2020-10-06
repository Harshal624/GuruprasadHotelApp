package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishModel;
import ace.infosolutions.guruprasadhotelapp.Captain.ItemAlertDialog;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ViewCartParcel;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCart;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;

public class FishListParcel extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener{
    private static final String FISH = "FISH";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private static final String PARCELS = "PARCELS";
    private RecyclerView recyclerView;
    private FishFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference fishRef,currentRef,parcelRef;
    private SharedPreferences sharedPreferences;

    private String doc_id;
    private FloatingActionButton check_cart;
    private ProgressBar progressBar;

    //TODO FISH PARCEL REMAINING

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_list);
        check_cart = (FloatingActionButton)findViewById(R.id.check_cart);
        db = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progressbar_fish);
        fishRef = db.collection(FISH);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_fishmain);
        layoutManager = new LinearLayoutManager(this);
        sharedPreferences = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        doc_id = sharedPreferences.getString(PARCEL_ID_KEY,"");
        currentRef = db.collection(PARCELS).document(doc_id).collection(CURRENT_KOT);
        parcelRef = db.collection(PARCELS);

        setUpRecyclerView();
        hideViewCartButton();

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FishModel model = documentSnapshot.toObject(FishModel.class);
                String item_title = model.getItem_title();
                String item_cost = "Rs."+model.getItem_cost();
                openDialog(item_title,item_cost);
            }
        });
        check_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCartParcel.class));
            }
        });

    }

    private void openDialog(String item_title, String item_cost) {
        ItemAlertDialog itemAlertDialog = new ItemAlertDialog(item_title,item_cost);
        itemAlertDialog.show(getSupportFragmentManager(),"dialog");
    }

    private void setUpRecyclerView() {
        Query query = fishRef;
        FirestoreRecyclerOptions<FishModel> fishOptions = new FirestoreRecyclerOptions.Builder<FishModel>()
                .setQuery(query,FishModel.class)
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
        //TODO ADDING FISH TO CURRENT_KOT AND UPDATING THE CURRENT_COST FIELD

        FoodItemModel model = new FoodItemModel(item_title,item_cost,qty);
        currentRef.document().set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    parcelRef.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                double current_cost = task.getResult().getDouble("current_cost");
                                double final_cost = item_cost + current_cost;
                                parcelRef.document(doc_id).update("current_cost",final_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            check_cart.setEnabled(true);
                                            Toast.makeText(FishListParcel.this, "Added!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });


    }

    private void hideViewCartButton() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 || dy < 0 && check_cart.isShown()){
                    check_cart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState == recyclerView.SCROLL_STATE_IDLE){
                    check_cart.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView,newState);
            }
        });
    }

}