package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally.CalculateTallyParcel.PARCELS;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.CURRENT_KOT;

public class ItemListParcel extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private String type;
    private String food_menu_title;
    private TextView food_menu_t;
    private FirebaseFirestore db;
    private ImageButton check_cart;
    private ProgressBar progressBar;
    private ImageView food_menu_icon;
    private SharedPreferences sharedPreferences;
    private String PARCEL_ID = "";
    private CollectionReference currentRef, parcelRef;

    private FishFirestoreAdapter adapter;
    private Query query;
    private CollectionReference coll_reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listparcel);
        db = FirebaseFirestore.getInstance();
        food_menu_icon = findViewById(R.id.food_menu_icon);
        progressBar = findViewById(R.id.progressbar_itemlist);
        food_menu_t = findViewById(R.id.food_menu_title);
        food_menu_title = getIntent().getStringExtra("TitleP");
        food_menu_t.setText(food_menu_title);
        recyclerView = findViewById(R.id.item_list_recycler);
        check_cart = findViewById(R.id.check_cart);

        //Getting document id from sharedpref
        sharedPreferences = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        PARCEL_ID = sharedPreferences.getString(PARCEL_ID_KEY, "");
        currentRef = db.collection(PARCELS).document(PARCEL_ID).collection(CURRENT_KOT);
        parcelRef = db.collection(PARCELS);
        //
        //getting the item type
        type = getIntent().getStringExtra("TypeP");
        //
        String COLL_NAME = getIntent().getStringExtra("CollName");
        //
        coll_reference = db.collection(Constants.FoodMenu).document(COLL_NAME).collection(type);
        query = coll_reference;

        check_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCartParcel.class));
            }
        });

        //
        switch (type) {
            case "starters_veg":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;
            case "papad":
                food_menu_icon.setImageResource(R.drawable.papad);
                break;

            case "starters_nonveg":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                break;

            case "colddrinkandstarters":
                food_menu_icon.setImageResource(R.drawable.colddrink);
                break;
            case "soup":
                food_menu_icon.setImageResource(R.drawable.soup);
                break;
            case "raytasalad":
                food_menu_icon.setImageResource(R.drawable.salad);
                break;
            case "veg_daal":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;
            case "veg_paneermaincourse":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;
            case "nonveg_egg":
                food_menu_icon.setImageResource(R.drawable.eggs);
                break;
            case "nonveg_specialthali":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                break;

            case "roti":
                food_menu_icon.setImageResource(R.drawable.roti);
                break;

            case "rice_biryani":
                food_menu_icon.setImageResource(R.drawable.biryani);
                break;

            case "rice_ricenoodles":
                food_menu_icon.setImageResource(R.drawable.ricenoodles);
                break;

            case "rice_main":
                food_menu_icon.setImageResource(R.drawable.rice);
                break;

            case "veg_vegmaincourse":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;

            case "nonveg_matanmaincourse":
                food_menu_icon.setImageResource(R.drawable.nonveg);

                break;

            case "springroll_chicken":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                break;

            case "springroll_veg":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;

            case "nonveg_chickenmaincourse":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                break;

        }
        //setting up the recyclerview of food items
        setupRecyclerView();
        //Hiding the viewcart floatingbutton while scrolling down the item list
        hideViewCartButton();

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FoodMenuModel model = documentSnapshot.toObject(FoodMenuModel.class);
                String item_title = model.getItem_title();
                String item_cost = "Rs." + model.getItem_cost();
                opendialog(item_title, item_cost);
            }
        });
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

    private void opendialog(String title, String cost) {
        ItemAlertDialog dialog = new ItemAlertDialog(title, cost);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        FirestoreRecyclerOptions<FoodMenuModel> foodOptions = new FirestoreRecyclerOptions.Builder<FoodMenuModel>()
                .setQuery(query, FoodMenuModel.class)
                .build();
        adapter = new FishFirestoreAdapter(foodOptions);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void applyText(final String item_title, final double item_cost, final int item_qty) {
        InternetConn conn = new InternetConn(this);
        if (conn.haveNetworkConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            check_cart.setEnabled(false);
            final FoodItemModel model = new FoodItemModel(item_title, item_cost, item_qty);
            final DocumentReference currRef = currentRef.document();
            final DocumentReference parcRef = parcelRef.document(PARCEL_ID);

            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(parcRef);
                    double stored_current_cost = snapshot.getDouble("current_cost");
                    double final_cost = stored_current_cost + item_cost;
                    transaction.set(currRef, model);
                    transaction.update(parcRef, "current_cost", final_cost);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility(View.GONE);
                    check_cart.setEnabled(true);
                    Toast.makeText(ItemListParcel.this, "Added", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

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



