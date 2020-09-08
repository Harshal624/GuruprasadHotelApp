package ace.infosolutions.guruprasadhotelapp.Captain;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

import ace.infosolutions.guruprasadhotelapp.R;

public class ItemList extends AppCompatActivity implements ItemAlertDialog.ItemAlertDialogListener {
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;
    private ArrayList<String> item_title;
    private ArrayList<String> item_cost;
    private ImageButton confirm_item,cancel_item;
    private String doc_id;
    private String type;
    private String food_menu_title;
    private TextView food_menu_t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        confirm_item = (ImageButton) findViewById(R.id.confirm_item);
        cancel_item = (ImageButton) findViewById(R.id.cancel_item);
        food_menu_t = (TextView)findViewById(R.id.food_menu_title);
        food_menu_title = getIntent().getStringExtra("Title");
        food_menu_t.setText(food_menu_title);
        recyclerView = (RecyclerView) findViewById(R.id.item_list_recycler);
        item_title = new ArrayList<>();
        item_cost = new ArrayList<>();
        //getting the item type
        type = getIntent().getStringExtra("Type");
        Log.e("Type",type);
        //
        doc_id = getIntent().getStringExtra("DocId");
        Log.e("DocId",doc_id);
        //checking doc id

        //
        switch (type){
            case "starters_veg": Collections.addAll(item_title,getResources().getStringArray(R.array.starters_veg_title));
                Collections.addAll(item_cost,getResources().getStringArray(R.array.starters_veg_cost));
                break;
            case "papad":  Collections.addAll(item_title,getResources().getStringArray(R.array.papad_title));
                Collections.addAll(item_cost,getResources().getStringArray(R.array.papad_cost));
                break;
        }

        //setting up the recyclerview of food items
        setupRecyclerView();

        itemListAdapter.setOnItemClickListener2(new ItemListAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick(String title, int position, String cost) {
               opendialog(title,cost);
            }
        });

        confirm_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(),FoodMenu.class));
            }
        });
    }

    private void opendialog(String title,String cost) {
        ItemAlertDialog dialog =  new ItemAlertDialog(title,cost);
        dialog.show(getSupportFragmentManager(),"dialog");
    }

    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        itemListAdapter = new ItemListAdapter(item_title,item_cost);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void applyText(String item_title, int item_cost, int qty) {
        Log.e("Title",item_title);
        Log.e("Cost:",String.valueOf(item_cost));
        Log.e("Qty:",String.valueOf(qty));
    }
}
