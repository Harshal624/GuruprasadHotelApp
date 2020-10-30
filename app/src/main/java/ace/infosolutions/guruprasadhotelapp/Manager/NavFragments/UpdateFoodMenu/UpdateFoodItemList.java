package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFoodMenu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FoodMenuModel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

public class UpdateFoodItemList extends AppCompatActivity {
    private ImageView food_menu_icon;
    private UpdateFoodMenuFirestoreAdapter adapter;
    private Query query;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton add;
    private String COLLECTION_NAME;
    private CollectionReference coll_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_food_item_list);
        food_menu_icon = findViewById(R.id.food_menu_icon);
        recyclerView = findViewById(R.id.recyclerview);

        String type = getIntent().getStringExtra("TypeP");
        String title = getIntent().getStringExtra("TitleP");
        COLLECTION_NAME = getIntent().getStringExtra("CollName");
        TextView title_tv = findViewById(R.id.title);
        title_tv.setText(title);

        coll_reference = db.collection(Constants.FoodMenu).document(COLLECTION_NAME).collection(type);
        query = coll_reference;

        add = findViewById(R.id.add_item);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new item based on type
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateFoodItemList.this);
                final AlertDialog alertDialog = builder.create();
                View view2 = LayoutInflater.from(UpdateFoodItemList.this).inflate(R.layout.addfoodmenuitem_alert, null);
                EditText food_title = view2.findViewById(R.id.food_item_title);
                EditText food_cost = view2.findViewById(R.id.food_item_cost);
                alertDialog.setView(view2);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });

        switch (type) {
            case "starters_veg":
                food_menu_icon.setImageResource(R.drawable.veg);
                break;
            case Constants.Papad:
                food_menu_icon.setImageResource(R.drawable.papad);
                break;

            case "starters_nonveg":
                food_menu_icon.setImageResource(R.drawable.nonveg);
                break;

            case "colddrinkandstarters":
                food_menu_icon.setImageResource(R.drawable.colddrink);
                break;

            case Constants.Soup:
                food_menu_icon.setImageResource(R.drawable.soup);
                break;
            case Constants.RaytaSalad:
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

            case Constants.Roti:
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
        setupRecyclerView();
        hideViewCartButton();
        adapter.setOnItemClickListener(new UpdateFoodMenuFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot) {
                //edit the item
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateFoodItemList.this);
                final AlertDialog alertDialog = builder.create();
                View view = LayoutInflater.from(UpdateFoodItemList.this).inflate(R.layout.editfoodmenuitem_alert, null);
                final EditText food_title = view.findViewById(R.id.food_item_title);
                final EditText food_cost = view.findViewById(R.id.food_item_cost);
                food_title.setHint(snapshot.getString("item_title"));
                food_cost.setHint(String.valueOf(snapshot.getDouble("item_cost")));
                alertDialog.setView(view);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //come here

                    }
                });
                alertDialog.show();
            }
        });
        adapter.setOnItemLongClickListener(new UpdateFoodMenuFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot) {
                //delete the item

            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        FirestoreRecyclerOptions<FoodMenuModel> foodOptions = new FirestoreRecyclerOptions.Builder<FoodMenuModel>()
                .setQuery(query, FoodMenuModel.class)
                .build();
        adapter = new UpdateFoodMenuFirestoreAdapter(foodOptions);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void hideViewCartButton() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && add.isShown()) {
                    add.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    add.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
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
