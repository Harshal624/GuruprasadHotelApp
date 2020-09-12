package ace.infosolutions.guruprasadhotelapp.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.util.Hex;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import ace.infosolutions.guruprasadhotelapp.MainActivity;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ConfirmOrderFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOTFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TableStatusFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFishPricesFragment;
import ace.infosolutions.guruprasadhotelapp.R;

public class Manager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageButton signout;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        toolbar = findViewById(R.id.manager_toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new RequestedKOTFragment()).commit();
            navigationView.setCheckedItem(R.id.requested_kot);
        }

    }

    private void managerSignout() {
        firebaseAuth.signOut();
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START) ){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
            finishAffinity();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.requested_kot: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
            ,new RequestedKOTFragment()).commit();
                break;
            case R.id.table_status:  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new TableStatusFragment()).commit();
                break;

            case R.id.update_fishprices: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new UpdateFishPricesFragment()).commit();
            break;

            case R.id.confirm_order:  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new ConfirmOrderFragment()).commit();
            break;
            case R.id.manager_signout: managerSignout();
            break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
