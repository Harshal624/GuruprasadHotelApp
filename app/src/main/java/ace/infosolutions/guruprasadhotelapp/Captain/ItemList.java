package ace.infosolutions.guruprasadhotelapp.Captain;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.ItemListAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCart;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

public class ItemList extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
    public static final String CURRENT_KOT = "CURRENT_KOT";
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private final String CUSTOMERS = "CUSTOMERS";
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;
    private ArrayList<String> item_title;
    private ArrayList<String> item_cost;
    private String type;
    private String food_menu_title;
    private TextView food_menu_t;
    private FirebaseFirestore db;
    private ImageButton check_cart;
    private ProgressBar progressBar;
    private ImageView food_menu_icon;
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        db = FirebaseFirestore.getInstance();
        castViews();

        //Getting document id from sharedpref
        sharedPreferences = getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY, "");
        //
        //getting the item type
        type = getIntent().getStringExtra("Type");
        //

        check_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCart.class));
            }
        });

        //
        switch (type) {
            case "starters_veg":
                food_menu_icon.setImageResource(R.drawable.veg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.starters_veg_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.starters_veg_cost));
                break;
            case "papad":
                food_menu_icon.setImageResource(R.drawable.papad);
                Collections.addAll(item_title, getResources().getStringArray(R.array.papad_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.papad_cost));
                break;

            case "starters_nonveg":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.starters_nonveg_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.starters_nonveg_cost));
                break;

            case "starters_colddrink":
                food_menu_icon.setImageResource(R.drawable.colddrink);
                Collections.addAll(item_title, getResources().getStringArray(R.array.starters_colddrink_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.starters_colddrink_cost));
                break;

            case "soup":
                food_menu_icon.setImageResource(R.drawable.soup);
                Collections.addAll(item_title, getResources().getStringArray(R.array.veg_nonveg_soup_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.veg_nonveg_soup_cost));
                break;
            case "raytasalad":
                food_menu_icon.setImageResource(R.drawable.salad);
                Collections.addAll(item_title, getResources().getStringArray(R.array.Rayata_Salad_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.Rayata_Salad_cost));
                break;
            case "veg_daal":
                food_menu_icon.setImageResource(R.drawable.veg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.daal_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.daal_cost));
                break;
            case "veg_paneermaincourse":
                food_menu_icon.setImageResource(R.drawable.veg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.paneer_maincourse_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.paneer_maincourse_cost));
                break;
            case "nonveg_egg":
                food_menu_icon.setImageResource(R.drawable.eggs);
                Collections.addAll(item_title, getResources().getStringArray(R.array.egg_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.egg_cost));
                break;
            case "nonveg_specialthali":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.nonveg_specialthali_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.nonveg_specialthali_cost));
                break;

            case "roti":
                food_menu_icon.setImageResource(R.drawable.roti);
                Collections.addAll(item_title, getResources().getStringArray(R.array.roti_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.roti_cost));
                break;

            case "rice_biryani":
                food_menu_icon.setImageResource(R.drawable.biryani);
                Collections.addAll(item_title, getResources().getStringArray(R.array.rice_biryani_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.rice_biryani_cost));
                break;

            case "rice_ricenoodles":
                food_menu_icon.setImageResource(R.drawable.ricenoodles);
                Collections.addAll(item_title, getResources().getStringArray(R.array.rice_ricenoodles_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.rice_ricenoodles_cost));
                break;

            case "rice_main":
                food_menu_icon.setImageResource(R.drawable.rice);
                Collections.addAll(item_title, getResources().getStringArray(R.array.rice_main_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.rice_main_cost));
                break;

            case "veg_vegmaincourse":
                food_menu_icon.setImageResource(R.drawable.veg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.veg_maincourse_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.veg_maincourse_cost));
                break;

            case "nonveg_matanmaincourse":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.nonveg_matanmaincourse_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.nonveg_matanmaincourse_cost));
                break;

            case "springroll_chicken":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.chickenspringroll_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.chickenspringroll_cost));
                break;

            case "springroll_veg":
                food_menu_icon.setImageResource(R.drawable.veg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.vegspringroll_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.vegspringroll_cost));
                break;

            case "nonveg_chickenmaincourse":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                Collections.addAll(item_title, getResources().getStringArray(R.array.chicken_maincourse_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.chicken_maincourse_cost));
                break;

        }
        //setting up the recyclerview of food items
        setupRecyclerView();
        //Hiding the viewcart floatingbutton while scrolling down the item list
        hideViewCartButton();

        itemListAdapter.setOnItemClickListener2(new ItemListAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick(String title, int position, String cost) {
                opendialog(title, cost);
            }
        });

    }

    private void castViews() {
        food_menu_icon = findViewById(R.id.food_menu_icon);
        progressBar = findViewById(R.id.progressbar_itemlist);
        food_menu_t = findViewById(R.id.food_menu_title);
        food_menu_title = getIntent().getStringExtra("Title");
        food_menu_t.setText(food_menu_title);
        recyclerView = findViewById(R.id.item_list_recycler);
        item_title = new ArrayList<>();
        item_cost = new ArrayList<>();
        check_cart = findViewById(R.id.check_cart);
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
        itemListAdapter = new ItemListAdapter(item_title, item_cost);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void applyText(final String item_title, final double item_cost, final int item_qty) {
        InternetConn conn = new InternetConn(ItemList.this);
        if (conn.haveNetworkConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            check_cart.setEnabled(false);
            final FoodItemModel model = new FoodItemModel(item_title, item_cost, item_qty);
            addItemToDB(model, item_cost);
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemToDB(final FoodItemModel model, final double item_cost) {
        final DocumentReference costRef = db.collection(CUSTOMERS).document(DOC_ID);
        final DocumentReference addItemRef = db.collection(CUSTOMERS).document(DOC_ID).collection(CURRENT_KOT).document();
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
                Toast.makeText(ItemList.this, "Added", Toast.LENGTH_SHORT).show();

            }
        });
    }

}



