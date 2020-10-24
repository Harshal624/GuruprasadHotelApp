package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import ace.infosolutions.guruprasadhotelapp.Captain.ui.main.SectionsPagerAdapter;
import ace.infosolutions.guruprasadhotelapp.MainActivity;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class CaptainMainFragment extends AppCompatActivity {
    private Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    private String MANAGER_UID;
    private String currentUID;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_main_fragment);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        currentUID = firebaseAuth.getUid();
        MANAGER_UID = this.getResources().getString(R.string.MANAGER_UID);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Customers");

    }

    @Override
    public void onBackPressed() {
        if (currentUID.equals(MANAGER_UID)) {
            finishAffinity();
            startActivity(new Intent(getApplicationContext(), Manager.class));
            overridePendingTransition(0, 0);
        } else {
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
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            firebaseAuth.signOut();
            finishAffinity();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }
}