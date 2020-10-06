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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.ItemListAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ItemAlertDialog;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ViewCartParcel;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;

public class ItemListParcel extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private static final String PARCELS = "PARCELS";
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
    private String PARCEL_ID = "";
    private CollectionReference currentRef, parcelRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listparcel);
        db = FirebaseFirestore.getInstance();
        food_menu_icon = findViewById(R.id.food_menu_icon);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_itemlist);
        food_menu_t = (TextView) findViewById(R.id.food_menu_title);
        food_menu_title = getIntent().getStringExtra("TitleP");
        food_menu_t.setText(food_menu_title);
        recyclerView = (RecyclerView) findViewById(R.id.item_list_recycler);
        item_title = new ArrayList<>();
        item_cost = new ArrayList<>();
        check_cart = (ImageButton) findViewById(R.id.check_cart);

        //Getting document id from sharedpref
        sharedPreferences = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        PARCEL_ID = sharedPreferences.getString(PARCEL_ID_KEY, "");
        currentRef = db.collection(PARCELS).document(PARCEL_ID).collection(CURRENT_KOT);
        parcelRef = db.collection(PARCELS);
        //
        //getting the item type
        type = getIntent().getStringExtra("TypeP");
        //

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
        InternetConn conn = new InternetConn(this);
        if (conn.haveNetworkConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            check_cart.setEnabled(false);
            FoodItemModel model = new FoodItemModel(item_title, item_cost, item_qty);
            currentRef.add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        parcelRef.document(PARCEL_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    double stored_current_cost = task.getResult().getDouble("current_cost");
                                    double final_cost = stored_current_cost + item_cost;
                                    parcelRef.document(PARCEL_ID).update("current_cost", final_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                check_cart.setEnabled(true);
                                                Toast.makeText(ItemListParcel.this, "Added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                check_cart.setEnabled(true);
                                                Toast.makeText(ItemListParcel.this, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    check_cart.setEnabled(true);
                                    Toast.makeText(ItemListParcel.this, "Failed!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }
            });

        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

}



