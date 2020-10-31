package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ace.infosolutions.guruprasadhotelapp.Captain.CaptainMainFragment;
import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FishList;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ViewCartParcel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

public class FoodMenuParcel extends AppCompatActivity implements View.OnClickListener {
    private Button starters_veg, starters_colddrink, starters_nonveg;
    private Button veg_daal, veg_paneermaincourse, veg_vegmaincourse;
    private Button nonveg_chickenmaincourse, nonveg_egg, nonveg_matanmaincourse, nonveg_fishmaincourse, nonveg_specialthali;
    private Button rice_main, rice_biryani, rice_ricenoodles;
    private Button springroll_chicken, springroll_veg;
    private CardView roti, soup, papad, raytasalad;
    private FloatingActionButton view_food_cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menuparcel);
        // Toolbar toolbar =  findViewById(R.id.toolbar);


        view_food_cart = findViewById(R.id.view_cart);
        //
        starters_veg = findViewById(R.id.starters_veg);
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
        rice_main = findViewById(R.id.rice_main);
        rice_main.setOnClickListener(this);
        rice_biryani = findViewById(R.id.rice_biryani);
        rice_biryani.setOnClickListener(this);
        rice_ricenoodles = findViewById(R.id.rice_ricenoodles);
        rice_ricenoodles.setOnClickListener(this);
        //
        springroll_chicken = findViewById(R.id.springroll_chicken);
        springroll_chicken.setOnClickListener(this);
        springroll_veg = findViewById(R.id.springroll_veg);
        springroll_veg.setOnClickListener(this);
        //
        roti = findViewById(R.id.roti);
        soup = findViewById(R.id.soup);
        papad = findViewById(R.id.papad);
        raytasalad = findViewById(R.id.raytasalad);
        roti.setOnClickListener(this);
        soup.setOnClickListener(this);
        papad.setOnClickListener(this);
        raytasalad.setOnClickListener(this);

        view_food_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewCartParcel.class));
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.starters_veg:
                gotolistActivity("starters_veg", "Veg Starters", Constants.Starters);
                break;
            case R.id.starters_colddrink:
                gotolistActivity("colddrinkandstarters", "Cold drink & Starters", Constants.Starters);
                break;
            case R.id.starters_nonveg:
                gotolistActivity("starters_nonveg", "Non-veg Starters", Constants.Starters);
                break;
            //
            case R.id.veg_daal:
                gotolistActivity("veg_daal", "Daal", Constants.Veg);
                break;
            case R.id.veg_paneermaincourse:
                gotolistActivity("veg_paneermaincourse", "Paneer Maincourse", Constants.Veg);
                break;
            case R.id.veg_vegmaincourse:
                gotolistActivity("veg_vegmaincourse", "Veg Maincourse", Constants.Veg);
                break;
            //
            case R.id.nonveg_chickenmaincourse:
                gotolistActivity("nonveg_chickenmaincourse", "Chicken Maicourse", Constants.NonVeg);
                break;
            case R.id.nonveg_egg:
                gotolistActivity("nonveg_egg", "Egg", Constants.NonVeg);
                break;
            case R.id.nonveg_fishmaincourse:
                startActivity(new Intent(getApplicationContext(), FishList.class));
                break;
            case R.id.nonveg_matanmaincourse:
                gotolistActivity("nonveg_matanmaincourse", "Matan Maincourse", Constants.NonVeg);
                break;
            case R.id.nonveg_specialthali:
                gotolistActivity("nonveg_specialthali", "Non-veg special thali", Constants.NonVeg);
                break;
            //
            case R.id.rice_biryani:
                gotolistActivity("rice_biryani", "Biryani Rice", Constants.Rice);
                break;
            case R.id.rice_main:
                gotolistActivity("rice_main", "Rice", Constants.Rice);
                break;
            case R.id.rice_ricenoodles:
                gotolistActivity("rice_ricenoodles", "Rice & Noodles", Constants.Rice);
                break;
            //
            case R.id.springroll_chicken:
                gotolistActivity("springroll_chicken", "Chicken Springroll", Constants.SpringRolls);
                break;
            case R.id.springroll_veg:
                gotolistActivity("springroll_veg", "Veg Springroll", Constants.SpringRolls);
                break;
            //
            case R.id.roti:
                gotolistActivity(Constants.Roti, "Roti", Constants.Roti);
                break;
            case R.id.soup:
                gotolistActivity(Constants.Soup, "Soup", Constants.Soup);
                break;
            case R.id.papad:
                gotolistActivity(Constants.Papad, "Papad", Constants.Papad);
                break;
            case R.id.raytasalad:
                gotolistActivity(Constants.RaytaSalad, "Rayta & Salad", Constants.RaytaSalad);
                break;
        }

    }

    private void gotolistActivity(final String id, final String food_title, String COLL_NAME) {
        Intent i = new Intent(getApplicationContext(), ItemListParcel.class);
        i.putExtra("TypeP", id);
        i.putExtra("TitleP", food_title);
        i.putExtra("CollName", COLL_NAME);
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
            finishAffinity();
            startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }
}
