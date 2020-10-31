package ace.infosolutions.guruprasadhotelapp.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import ace.infosolutions.guruprasadhotelapp.Captain.CaptainMainFragment;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelFragment;
import ace.infosolutions.guruprasadhotelapp.MainActivity;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ChangeManagerPin;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.CustomerListFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.History.HistoryFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ParcelHistory.ParcelHistoryFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel.TallyExcel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFishPrices.UpdateFishPricesFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFoodMenu.UpdateFoodMenuFragment;
import ace.infosolutions.guruprasadhotelapp.R;

public class Manager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public Toolbar toolbar;
    private ImageButton signout;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private long backPressedTime;
    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        toolbar = findViewById(R.id.manager_toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new CustomerListFragment()).commit();
            navigationView.setCheckedItem(R.id.customer_list);
        }

    }

    private void managerSignout() {
        firebaseAuth.signOut();
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            if (navigationView.getMenu().findItem(R.id.customer_list).isChecked()) {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel();
                    finishAffinity();
                    finish();
                    super.onBackPressed();
                    return;
                } else {
                    backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                }
                backPressedTime = System.currentTimeMillis();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new CustomerListFragment()).commit();
                navigationView.setCheckedItem(R.id.customer_list);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_fishprices:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new UpdateFishPricesFragment()).commit();
                break;
            case R.id.manager_signout:
                managerSignout();
                break;
            case R.id.customer_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new CustomerListFragment()).commit();
                break;
            case R.id.take_order:
                startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
                break;

            case R.id.parcel:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ParcelFragment()).commit();
                break;

            case R.id.cust_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new HistoryFragment()).commit();
                break;

            case R.id.tally:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new TallyExcel()).commit();
                break;

            case R.id.parcel_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ParcelHistoryFragment()).commit();
                break;

            case R.id.manager_pin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ChangeManagerPin()).commit();
                break;

            case R.id.update_foodmenu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new UpdateFoodMenuFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
