package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ace.infosolutions.guruprasadhotelapp.MainActivity;
import ace.infosolutions.guruprasadhotelapp.R;

public class FoodMenu extends AppCompatActivity implements View.OnClickListener{
    String table_type="",table_no="",doc_id="";
    private Button starters_veg,starters_colddrink,starters_nonveg;
    private Button veg_daal,veg_paneermaincourse,veg_vegmaincourse;
    private Button nonveg_chickenmaincourse,nonveg_egg,nonveg_matanmaincourse,nonveg_fishmaincourse,nonveg_specialthali;
    private Button rice_main,rice_biryani,rice_ricenoodles;
    private Button springroll_chicken,springroll_veg;
    private CardView roti,soup,papad,raytasalad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //
        starters_veg = (Button)findViewById(R.id.starters_veg);
        starters_veg.setOnClickListener(this);

        starters_colddrink = (Button)findViewById(R.id.starters_colddrink);
        starters_colddrink.setOnClickListener(this);
        starters_nonveg = (Button)findViewById(R.id.starters_nonveg);
        starters_nonveg.setOnClickListener(this);
        //
        veg_daal = (Button)findViewById(R.id.veg_daal);
        veg_daal.setOnClickListener(this);
        veg_paneermaincourse = (Button)findViewById(R.id.veg_paneermaincourse);
        veg_paneermaincourse.setOnClickListener(this);
        veg_vegmaincourse = (Button)findViewById(R.id.veg_vegmaincourse);
        veg_vegmaincourse.setOnClickListener(this);
        //
        nonveg_chickenmaincourse = (Button)findViewById(R.id.nonveg_chickenmaincourse);
        nonveg_chickenmaincourse.setOnClickListener(this);
        nonveg_egg = (Button)findViewById(R.id.nonveg_egg);
        nonveg_egg.setOnClickListener(this);
        nonveg_matanmaincourse = (Button)findViewById(R.id.nonveg_matanmaincourse);
        nonveg_matanmaincourse.setOnClickListener(this);
        nonveg_fishmaincourse = (Button)findViewById(R.id.nonveg_fishmaincourse);
        nonveg_fishmaincourse.setOnClickListener(this);
        nonveg_specialthali = (Button)findViewById(R.id.nonveg_specialthali);
        nonveg_specialthali.setOnClickListener(this);
        //
        rice_main = (Button)findViewById(R.id.rice_main);
        rice_main.setOnClickListener(this);
        rice_biryani = (Button)findViewById(R.id.rice_biryani);
        rice_biryani.setOnClickListener(this);
        rice_ricenoodles = (Button)findViewById(R.id.rice_ricenoodles);
        rice_ricenoodles.setOnClickListener(this);
        //
        springroll_chicken = (Button)findViewById(R.id.springroll_chicken);
        springroll_chicken.setOnClickListener(this);
        springroll_veg = (Button)findViewById(R.id.springroll_veg);
        springroll_veg.setOnClickListener(this);
        //
        roti = (CardView)findViewById(R.id.roti);
        soup = (CardView)findViewById(R.id.soup);
        papad = (CardView)findViewById(R.id.papad);
        raytasalad = (CardView)findViewById(R.id.raytasalad);
        roti.setOnClickListener(this);
        soup.setOnClickListener(this);
        papad.setOnClickListener(this);
        raytasalad.setOnClickListener(this);

        //setting up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Food Menu");
        try{
            table_type = getIntent().getStringExtra("Table_type");
            table_no = getIntent().getStringExtra("Table_no");
            doc_id = getIntent().getStringExtra("ID");
            Log.e("TableType:",table_type);
            Log.e("TableNo:",table_no);
            Log.e("DocId:",doc_id);

        }
        catch (NullPointerException e){
                doc_id = getIntent().getStringExtra("DocumentId");
                Log.e("Nullpointer1",e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        startActivity(new Intent(getApplicationContext(),CaptainMainFragment.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.starters_veg: gotolistActivity("starters_veg","Veg Starters");
                break;
            case R.id.starters_colddrink: gotolistActivity("starters_colddrink","Cold drink & Starters");
                break;
            case R.id.starters_nonveg: gotolistActivity("starters_nonveg","Non-veg Starters");
                break;
                //
            case R.id.veg_daal: gotolistActivity("veg_daal", "Daal");
                break;
            case R.id.veg_paneermaincourse: gotolistActivity("veg_paneermaincourse","Paneer Maincourse");
                break;
            case R.id.veg_vegmaincourse: gotolistActivity("veg_vegmaincourse","Veg Maincourse");
                break;
                //
            case R.id.nonveg_chickenmaincourse: gotolistActivity("nonveg_chickenmaincourse","Chicken Maicourse");
                break;
            case R.id.nonveg_egg: gotolistActivity("nonveg_egg","Egg");
                break;
            case R.id.nonveg_fishmaincourse: gotolistActivity("nonveg_fishmaincourse","Fish Maincourse");
                break;
            case R.id.nonveg_matanmaincourse: gotolistActivity("nonveg_matanmaincourse","Matan Maincourse");
                break;
            case R.id.nonveg_specialthali: gotolistActivity("nonveg_specialthali","Non-veg special thali");
                break;
                //
            case R.id.rice_biryani: gotolistActivity("rice_biryani","Biryani Rice");
                break;
            case R.id.rice_main: gotolistActivity("rice_main","Rice");
                break;
            case R.id.rice_ricenoodles: gotolistActivity("rice_ricenoodles","Rice & Noodles");
                break;
                //
            case R.id.springroll_chicken: gotolistActivity("springroll_chicken","Chicken Springroll");
                break;
            case R.id.springroll_veg: gotolistActivity("springroll_veg","Veg Springroll");
                break;
                //
            case R.id.roti: gotolistActivity("roti","Roti");
                break;
            case R.id.soup: gotolistActivity("soup","Soup");
                break;
            case R.id.papad: gotolistActivity("papad","Papad");
                break;
            case R.id.raytasalad: gotolistActivity("raytasalad","Rayta & Salad");
                break;

        }

    }
    private void gotolistActivity(String id,String food_title){
        Intent i = new Intent(getApplicationContext(),ItemList.class);
        i.putExtra("TableType",table_type);
        i.putExtra("TableNo",table_no);
        i.putExtra("DocId",doc_id);
        i.putExtra("Type",id);
        i.putExtra("Title",food_title);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cancel_order){
            finishAffinity();
            startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
        }
        return super.onOptionsItemSelected(item);
    }
}