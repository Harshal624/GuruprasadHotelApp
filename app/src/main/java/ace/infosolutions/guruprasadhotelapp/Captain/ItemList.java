package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.ItemListAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCart;
import ace.infosolutions.guruprasadhotelapp.R;

public class ItemList extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
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
    //String doc_id = "XETFYhw96vAF2BM18Pqe";

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private SharedPreferences sharedPreferences;

    private String DOC_ID = "";
    private final String CUSTOMER = "Customers";
    private final String KOT = "KOT";
    private final String FINAL_BILL = "FINAL_BILL";
    private final String COST = "COST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        db = FirebaseFirestore.getInstance();
        food_menu_t = (TextView) findViewById(R.id.food_menu_title);
        food_menu_title = getIntent().getStringExtra("Title");
        food_menu_t.setText(food_menu_title);
        recyclerView = (RecyclerView) findViewById(R.id.item_list_recycler);
        item_title = new ArrayList<>();
        item_cost = new ArrayList<>();
        check_cart = (ImageButton)findViewById(R.id.check_cart);

        //Getting document id from sharedpref
        sharedPreferences = getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
         DOC_ID = sharedPreferences.getString(DOC_ID_KEY,"");
         //
        //getting the item type
        type = getIntent().getStringExtra("Type");
        Log.e("Type", type);
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
                Collections.addAll(item_title, getResources().getStringArray(R.array.starters_veg_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.starters_veg_cost));
                break;
            case "papad":
                Collections.addAll(item_title, getResources().getStringArray(R.array.papad_title));
                Collections.addAll(item_cost, getResources().getStringArray(R.array.papad_cost));
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
    public void applyText(final String item_title, final long item_cost, final int qty) {

        //TODO 1.Add items to KOT Collection and FINAL_BILL Collection
        //TODO 2.Update Cost Collection
        boolean isrequested = false;
        check_cart.setEnabled(false);

        FoodItemPOJO items= new FoodItemPOJO(item_title,item_cost,qty,isrequested);

        db.collection(CUSTOMER).document(DOC_ID)
                .collection(KOT)
                .document()
                .set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    addToFinalBill(item_title,item_cost,qty);

                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemList.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addToFinalBill(String itemtitle, final double itemcost, int itmeqty) {
        boolean isconfirmed = false;
        boolean isrequested = false;
        //TODO Adding confirmed food items to final bill
        FinalBillPOJO finalBillPOJO = new FinalBillPOJO(itemtitle,itemcost,itmeqty,isrequested,isconfirmed);
        db.collection(CUSTOMER).document(DOC_ID)
                .collection(FINAL_BILL).add(finalBillPOJO)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        updateCost(itemcost);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemList.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCost(final double item_cost) {

        db.collection(CUSTOMER).document(DOC_ID)
                .collection(COST)
                .document(COST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = 0;
                    try {
                        cost = snapshot.getDouble("cost");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    cost = cost + item_cost;
                    Map<String,Double> map = new HashMap<>();
                    map.put("cost",cost);
                    db.collection(CUSTOMER).document(DOC_ID)
                            .collection(COST)
                            .document(COST).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            check_cart.setEnabled(true);
                            Toast.makeText(ItemList.this, "Added", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    public class FoodItemPOJO{
        private String item_title;
        private double item_cost;
        private int item_qty;
        private boolean isrequested;

        public FoodItemPOJO() {

        }

        public FoodItemPOJO(String item_title, double item_cost, int item_qty, boolean isrequested) {
            this.item_title = item_title;
            this.item_cost = item_cost;
            this.item_qty = item_qty;
            this.isrequested = isrequested;
        }

        public String getItem_title() {
            return item_title;
        }

        public double getItem_cost() {
            return item_cost;
        }

        public int getItem_qty() {
            return item_qty;
        }

        public boolean isIsrequested() {
            return isrequested;
        }
    }

    public class FinalBillPOJO {
        private String item_title;
        private double item_cost;
        private int item_qty;
        private boolean isrequested;
        private boolean isconfirmed;

        FinalBillPOJO(){}


        public String getItem_title() {
            return item_title;
        }

        public double getItem_cost() {
            return item_cost;
        }

        public int getItem_qty() {
            return item_qty;
        }

        public boolean isIsrequested() {
            return isrequested;
        }

        public boolean isIsconfirmed() {
            return isconfirmed;
        }

        public FinalBillPOJO(String item_title, double item_cost, int item_qty, boolean isrequested, boolean isconfirmed) {
            this.item_title = item_title;
            this.item_cost = item_cost;
            this.item_qty = item_qty;
            this.isrequested = isrequested;
            this.isconfirmed = isconfirmed;
        }
    }

}



