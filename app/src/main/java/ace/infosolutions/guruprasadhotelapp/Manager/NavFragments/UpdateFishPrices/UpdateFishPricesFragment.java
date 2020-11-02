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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FoodMenuModel;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

public class UpdateFishPricesFragment extends Fragment {
    private static final String FISH = "FISH";
    private static final String CUSTOMERS = "Customers";
    private static final String KOT = "KOT";
    private static final String FINAL_BILL = "FINAL_BILL";
    private static final String COST = "COST";
    String fish_doc_id;
    private RecyclerView recyclerView;
    private FishFirestoreAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference fishRef;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private EditText enter_cost;
    private View view1;
    private InternetConn conn;

    private EditText enter_title, enter_title_english;
    private String fish_title, fish_title_english;

    private FloatingActionButton add;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_update_fish_prices, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Update Fish Prices");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_updatefish);
        add = view.findViewById(R.id.add_fish);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        fishRef = db.collection(FISH);
        view1 = inflater.inflate(R.layout.updatefish_alertdialog, null);
        builder = new AlertDialog.Builder(getContext());
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        enter_cost = (EditText) view1.findViewById(R.id.fish_cost);
        enter_title = view1.findViewById(R.id.fish_item_title);
        enter_title_english = view1.findViewById(R.id.fish_item_title_english);


        conn = new InternetConn(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerview();
        hideViewCartButton();

        setupAlertDialog();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View addView = LayoutInflater.from(getContext()).inflate(R.layout.addfoodmenuitem_alert, null);
                final EditText food_title = addView.findViewById(R.id.food_item_title);
                final EditText food_cost = addView.findViewById(R.id.food_item_cost);
                final EditText food_title_english = addView.findViewById(R.id.food_item_title_english);


                final AlertDialog alertDialog2 = builder.create();
                alertDialog2.setView(addView);
                alertDialog2.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog2.dismiss();
                    }
                });

                alertDialog2.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String foodTitle = food_title.getText().toString().trim();
                        String foodCost = food_cost.getText().toString().trim();
                        String foodTitleEnglish = food_title_english.getText().toString().trim();
                        if (foodTitle.isEmpty() || foodCost.isEmpty() || foodTitleEnglish.isEmpty()) {
                            alertDialog2.dismiss();
                            Toast.makeText(getContext(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                        } else {
                            double cost_food = Double.parseDouble(foodCost);
                            FoodMenuModel model = new FoodMenuModel(foodTitle, cost_food, foodTitleEnglish);
                            fishRef.add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), foodTitle + " " + "added", Toast.LENGTH_SHORT).show();
                                    alertDialog2.dismiss();
                                }
                            });
                        }
                    }
                });
                alertDialog2.show();
            }
        });

        adapter.setOnItemLongClickListener(new FishFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot) {
                final String id = documentSnapshot.getId();
                final AlertDialog deleteDialog = builder.create();
                FoodMenuModel model = documentSnapshot.toObject(FoodMenuModel.class);
                deleteDialog.setTitle("Delete" + " " + model.getItem_title() + "?");
                deleteDialog.setMessage("Are you sure want to delete this item?");
                deleteDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fishRef.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                deleteDialog.show();
            }
        });

        adapter.setOnItemClickListener(new FishFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                enter_cost.setText(null);
                enter_title.setText(null);
                enter_title_english.setText(null);
                FoodMenuModel foodMenuModel = documentSnapshot.toObject(FoodMenuModel.class);
                fish_doc_id = documentSnapshot.getId();
                String fish_title = foodMenuModel.getItem_title();
                alertDialog.setTitle(fish_title);
                if (conn.haveNetworkConnection())
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
                fish_title = enter_title.getText().toString().trim();
                fish_title_english = enter_title_english.getText().toString().trim();
                if (!fish_title.isEmpty() && !fish_title_english.isEmpty() && !fish_cost.isEmpty()) {
                    WriteBatch batch = db.batch();
                    batch.update(fishRef.document(fish_doc_id), "item_title", fish_title);
                    batch.update(fishRef.document(fish_doc_id), "item_title_english", fish_title);
                    double cost_fish = Double.parseDouble(fish_cost);
                    batch.update(fishRef.document(fish_doc_id), "item_cost", cost_fish);
                    batch.commit();
                } else if (fish_title.isEmpty() && fish_title_english.isEmpty() && fish_cost.isEmpty()) {
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Nothing updated!", Toast.LENGTH_SHORT).show();
                } else if (!fish_title.isEmpty() && fish_title_english.isEmpty() && fish_cost.isEmpty()) {
                    fishRef.document(fish_doc_id).update("item_title", fish_title);
                } else if (fish_title.isEmpty() && !fish_title_english.isEmpty() && fish_cost.isEmpty()) {
                    fishRef.document(fish_doc_id).update("item_title_english", fish_title_english);
                } else if (fish_title.isEmpty() && fish_title_english.isEmpty() && !fish_cost.isEmpty()) {
                    double cost_fish = Double.parseDouble(fish_cost);
                    fishRef.document(fish_doc_id).update("item_cost", cost_fish);
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Only one quantity can be updated at a time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupRecyclerview() {
        Query query = fishRef;
        FirestoreRecyclerOptions<FoodMenuModel> updateFishOptions = new
                FirestoreRecyclerOptions.Builder<FoodMenuModel>()
                .setQuery(query, FoodMenuModel.class)
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

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
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
}
