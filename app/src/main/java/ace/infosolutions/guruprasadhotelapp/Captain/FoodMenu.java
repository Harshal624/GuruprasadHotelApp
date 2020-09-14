package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCart;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartPOJO;
import ace.infosolutions.guruprasadhotelapp.R;

public class FoodMenu extends AppCompatActivity implements View.OnClickListener {
    private Button starters_veg, starters_colddrink, starters_nonveg;
    private Button veg_daal, veg_paneermaincourse, veg_vegmaincourse;
    private Button nonveg_chickenmaincourse, nonveg_egg, nonveg_matanmaincourse, nonveg_fishmaincourse, nonveg_specialthali;
    private Button rice_main, rice_biryani, rice_ricenoodles;
    private Button springroll_chicken, springroll_veg;
    private CardView roti, soup, papad, raytasalad;
    private ImageButton view_food_cart, generate_KOT;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private FirebaseFirestore db;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String TABLE_TYPE_KEY = "TABLE_TYPE_KEY";
    private static final String TABLE_NO_KEY = "TABLE_NO_KEY";
    private SharedPreferences sharedPreferences;

    private static final String KOT = "KOT";
    private static final String COST = "COST";
    private static final String CUSTOMERS = "Customers";
    private String doc_id,table_type;
    private int table_no;
    private Map<String,Object> isrequested_map;
    private Map<String,Object> update_reqKOT;
    private Map<String,Object> update_parentKOT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        doc_id = sharedPreferences.getString(DOC_ID_KEY,"");
        isrequested_map = new HashMap<>();
        update_reqKOT = new HashMap<>();
        update_parentKOT = new HashMap<>();

        isrequested_map.put("isrequested",true);
        table_no = sharedPreferences.getInt(TABLE_NO_KEY,0);
        table_type = sharedPreferences.getString(TABLE_TYPE_KEY,"");
        update_reqKOT.put(String.valueOf(table_no),true);
        update_parentKOT.put("kotrequested",true);


        view_food_cart = (ImageButton) findViewById(R.id.view_cart);
        generate_KOT = (ImageButton) findViewById(R.id.save_cart);
        //
        starters_veg = (Button) findViewById(R.id.starters_veg);
        starters_veg.setOnClickListener(this);

        starters_colddrink = (Button) findViewById(R.id.starters_colddrink);
        starters_colddrink.setOnClickListener(this);
        starters_nonveg = (Button) findViewById(R.id.starters_nonveg);
        starters_nonveg.setOnClickListener(this);
        //
        veg_daal = (Button) findViewById(R.id.veg_daal);
        veg_daal.setOnClickListener(this);
        veg_paneermaincourse = (Button) findViewById(R.id.veg_paneermaincourse);
        veg_paneermaincourse.setOnClickListener(this);
        veg_vegmaincourse = (Button) findViewById(R.id.veg_vegmaincourse);
        veg_vegmaincourse.setOnClickListener(this);
        //
        nonveg_chickenmaincourse = (Button) findViewById(R.id.nonveg_chickenmaincourse);
        nonveg_chickenmaincourse.setOnClickListener(this);
        nonveg_egg = (Button) findViewById(R.id.nonveg_egg);
        nonveg_egg.setOnClickListener(this);
        nonveg_matanmaincourse = (Button) findViewById(R.id.nonveg_matanmaincourse);
        nonveg_matanmaincourse.setOnClickListener(this);
        nonveg_fishmaincourse = (Button) findViewById(R.id.nonveg_fishmaincourse);
        nonveg_fishmaincourse.setOnClickListener(this);
        nonveg_specialthali = (Button) findViewById(R.id.nonveg_specialthali);
        nonveg_specialthali.setOnClickListener(this);
        //
        rice_main = (Button) findViewById(R.id.rice_main);
        rice_main.setOnClickListener(this);
        rice_biryani = (Button) findViewById(R.id.rice_biryani);
        rice_biryani.setOnClickListener(this);
        rice_ricenoodles = (Button) findViewById(R.id.rice_ricenoodles);
        rice_ricenoodles.setOnClickListener(this);
        //
        springroll_chicken = (Button) findViewById(R.id.springroll_chicken);
        springroll_chicken.setOnClickListener(this);
        springroll_veg = (Button) findViewById(R.id.springroll_veg);
        springroll_veg.setOnClickListener(this);
        //
        roti = (CardView) findViewById(R.id.roti);
        soup = (CardView) findViewById(R.id.soup);
        papad = (CardView) findViewById(R.id.papad);
        raytasalad = (CardView) findViewById(R.id.raytasalad);
        roti.setOnClickListener(this);
        soup.setOnClickListener(this);
        papad.setOnClickListener(this);
        raytasalad.setOnClickListener(this);

        view_food_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCart.class));
            }
        });

        setUpAlertDialog();
        //setting up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Food Menu");


        generate_KOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate_KOT.setEnabled(false);
                requestKOT();

            }
        });
    }

    private void requestKOT() {
        //TODO SET isrequested field to true

        db.collection(CUSTOMERS).document(doc_id).collection(KOT).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                String KOT_doc_id = snapshot.getId();
                                db.collection(CUSTOMERS).document(doc_id).collection(KOT).document(KOT_doc_id)
                                        .update(isrequested_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FoodMenu.this, "Failed to request KOT", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            updateparentdocKOT();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FoodMenu.this, "Cannot request KOT for the given items", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void updateparentdocKOT() {
      db.collection(CUSTOMERS).document(doc_id).update(update_parentKOT).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(FoodMenu.this, "KOT Request sent successfully", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FoodMenu.this, "Unable to generate KOT", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setUpAlertDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure want to cancel the order?")
                .setMessage("All items will be removed")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setIcon(R.drawable.ic_shopping_cart2);
    }

    @Override
    public void onBackPressed() {
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.starters_veg:
                gotolistActivity("starters_veg", "Veg Starters");
                break;
            case R.id.starters_colddrink:
                gotolistActivity("starters_colddrink", "Cold drink & Starters");
                break;
            case R.id.starters_nonveg:
                gotolistActivity("starters_nonveg", "Non-veg Starters");
                break;
            //
            case R.id.veg_daal:
                gotolistActivity("veg_daal", "Daal");
                break;
            case R.id.veg_paneermaincourse:
                gotolistActivity("veg_paneermaincourse", "Paneer Maincourse");
                break;
            case R.id.veg_vegmaincourse:
                gotolistActivity("veg_vegmaincourse", "Veg Maincourse");
                break;
            //
            case R.id.nonveg_chickenmaincourse:
                gotolistActivity("nonveg_chickenmaincourse", "Chicken Maicourse");
                break;
            case R.id.nonveg_egg:
                gotolistActivity("nonveg_egg", "Egg");
                break;
            case R.id.nonveg_fishmaincourse:
                gotolistActivity("nonveg_fishmaincourse", "Fish Maincourse");
                break;
            case R.id.nonveg_matanmaincourse:
                gotolistActivity("nonveg_matanmaincourse", "Matan Maincourse");
                break;
            case R.id.nonveg_specialthali:
                gotolistActivity("nonveg_specialthali", "Non-veg special thali");
                break;
            //
            case R.id.rice_biryani:
                gotolistActivity("rice_biryani", "Biryani Rice");
                break;
            case R.id.rice_main:
                gotolistActivity("rice_main", "Rice");
                break;
            case R.id.rice_ricenoodles:
                gotolistActivity("rice_ricenoodles", "Rice & Noodles");
                break;
            //
            case R.id.springroll_chicken:
                gotolistActivity("springroll_chicken", "Chicken Springroll");
                break;
            case R.id.springroll_veg:
                gotolistActivity("springroll_veg", "Veg Springroll");
                break;
            //
            case R.id.roti:
                gotolistActivity("roti", "Roti");
                break;
            case R.id.soup:
                gotolistActivity("soup", "Soup");
                break;
            case R.id.papad:
                gotolistActivity("papad", "Papad");
                break;
            case R.id.raytasalad:
                gotolistActivity("raytasalad", "Rayta & Salad");
                break;
        }

    }

    private void gotolistActivity(final String id, final String food_title) {
        Intent i = new Intent(getApplicationContext(), ItemList.class);
        i.putExtra("Type", id);
        i.putExtra("Title", food_title);
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel_order) {
            alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection(CUSTOMERS).document(doc_id).collection(COST).document(COST)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double cost = documentSnapshot.getDouble("cost");
                if(cost == 0){
                    generate_KOT.setEnabled(false);
                }
                else{
                    generate_KOT.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                generate_KOT.setEnabled(false);
            }
        });
    }
}
