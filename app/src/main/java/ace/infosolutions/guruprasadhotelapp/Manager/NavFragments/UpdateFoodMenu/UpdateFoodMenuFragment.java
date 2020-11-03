package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFoodMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;

public class UpdateFoodMenuFragment extends Fragment implements View.OnClickListener {
    private Button starters_veg, starters_colddrink, starters_nonveg;
    private Button veg_daal, veg_paneermaincourse, veg_vegmaincourse;
    private Button nonveg_chickenmaincourse, nonveg_egg, nonveg_matanmaincourse, nonveg_fishmaincourse, nonveg_specialthali;
    private Button rice_main, rice_biryani, rice_ricenoodles;
    private Button springroll_chicken, springroll_veg;
    private CardView roti, soup, papad, raytasalad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_menu, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Update Food Menu");
        FloatingActionButton view_cart = view.findViewById(R.id.view_cart);
        view_cart.setVisibility(View.GONE);
        loadImages(view);
        castViews(view);
        return view;
    }

    private void loadImages(View view) {
        ImageView foodback = view.findViewById(R.id.foodmenuimage);
        Picasso.get().load(R.drawable.foodmenuback).fit().into(foodback);
        ImageView starters = view.findViewById(R.id.starters);
        Picasso.get().load(R.drawable.starters).fit().into(starters);

        ImageView veg = view.findViewById(R.id.veg);
        Picasso.get().load(R.drawable.veg).fit().into(veg);
        ImageView nonveg = view.findViewById(R.id.nonveg);
        Picasso.get().load(R.drawable.nonveg).fit().into(nonveg);

        ImageView rice = view.findViewById(R.id.rice);
        Picasso.get().load(R.drawable.rice).fit().into(rice);
        ImageView papad = view.findViewById(R.id.papad2);
        Picasso.get().load(R.drawable.papad).fit().into(papad);

        ImageView roti = view.findViewById(R.id.roti2);
        Picasso.get().load(R.drawable.roti).fit().into(roti);
        ImageView soup = view.findViewById(R.id.soup2);
        Picasso.get().load(R.drawable.soup).fit().into(soup);

        ImageView salad2 = view.findViewById(R.id.salad2);
        Picasso.get().load(R.drawable.salad).fit().into(salad2);

        ImageView springroll2 = view.findViewById(R.id.springroll2);
        Picasso.get().load(R.drawable.springroll).fit().into(springroll2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void gotolistActivity(final String id, final String food_title, String collection_name) {
        Intent i = new Intent(getContext(), UpdateFoodItemList.class);
        i.putExtra("TypeP", id);
        i.putExtra("TitleP", food_title);
        i.putExtra("CollName", collection_name);
        startActivity(i);
    }

    private void castViews(View view) {

        starters_veg = view.findViewById(R.id.starters_veg);
        starters_veg.setOnClickListener(this);

        starters_colddrink = (Button) view.findViewById(R.id.starters_colddrink);
        starters_colddrink.setOnClickListener(this);
        starters_nonveg = (Button) view.findViewById(R.id.starters_nonveg);
        starters_nonveg.setOnClickListener(this);
        //
        veg_daal = (Button) view.findViewById(R.id.veg_daal);
        veg_daal.setOnClickListener(this);
        veg_paneermaincourse = (Button) view.findViewById(R.id.veg_paneermaincourse);
        veg_paneermaincourse.setOnClickListener(this);
        veg_vegmaincourse = (Button) view.findViewById(R.id.veg_vegmaincourse);
        veg_vegmaincourse.setOnClickListener(this);
        //
        nonveg_chickenmaincourse = (Button) view.findViewById(R.id.nonveg_chickenmaincourse);
        nonveg_chickenmaincourse.setOnClickListener(this);
        nonveg_egg = (Button) view.findViewById(R.id.nonveg_egg);
        nonveg_egg.setOnClickListener(this);
        nonveg_matanmaincourse = (Button) view.findViewById(R.id.nonveg_matanmaincourse);
        nonveg_matanmaincourse.setOnClickListener(this);
        nonveg_fishmaincourse = (Button) view.findViewById(R.id.nonveg_fishmaincourse);
        nonveg_fishmaincourse.setOnClickListener(this);
        nonveg_specialthali = (Button) view.findViewById(R.id.nonveg_specialthali);
        nonveg_specialthali.setOnClickListener(this);
        //
        rice_main = view.findViewById(R.id.rice_main);
        rice_main.setOnClickListener(this);
        rice_biryani = view.findViewById(R.id.rice_biryani);
        rice_biryani.setOnClickListener(this);
        rice_ricenoodles = view.findViewById(R.id.rice_ricenoodles);
        rice_ricenoodles.setOnClickListener(this);
        //
        springroll_chicken = view.findViewById(R.id.springroll_chicken);
        springroll_chicken.setOnClickListener(this);
        springroll_veg = view.findViewById(R.id.springroll_veg);
        springroll_veg.setOnClickListener(this);
        //
        roti = view.findViewById(R.id.roti);
        soup = view.findViewById(R.id.soup);
        papad = view.findViewById(R.id.papad);
        raytasalad = view.findViewById(R.id.raytasalad);
        roti.setOnClickListener(this);
        soup.setOnClickListener(this);
        papad.setOnClickListener(this);
        raytasalad.setOnClickListener(this);
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
                Toast.makeText(getContext(), "There is seperate section to update fish price", Toast.LENGTH_SHORT).show();
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
                gotolistActivity("roti", "Roti", Constants.Roti);
                break;
            case R.id.soup:
                gotolistActivity("soup", "Soup", Constants.Soup);
                break;
            case R.id.papad:
                gotolistActivity("papad", "Papad", Constants.Papad);
                break;
            case R.id.raytasalad:
                gotolistActivity("raytasalad", "Rayta & Salad", Constants.RaytaSalad);
                break;
        }


    }
}
