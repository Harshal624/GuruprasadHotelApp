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
    FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private String MANAGER_UID;
    private String currentUID;
    private long backPressedTime;
    private Toast backToast;
    //  private SharedPreferences printerSharedPref;
    //  private String printerString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_main_fragment);
        firebaseAuth = FirebaseAuth.getInstance();
        //  printerSharedPref = getSharedPreferences(Constants.SP_PRINTER, MODE_PRIVATE);
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
            startActivity(new Intent(CaptainMainFragment.this, Manager.class));
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
            startActivity(new Intent(CaptainMainFragment.this, MainActivity.class));
            overridePendingTransition(0, 0);
        } /*else if (item.getItemId() == R.id.printerName) {
            printerString = printerSharedPref.getString(Constants.SP_PRINTER_NAME, "");
            AlertDialog.Builder builder = new AlertDialog.Builder(CaptainMainFragment.this);
            final AlertDialog alertDialog = builder.create();
            View view = LayoutInflater.from(CaptainMainFragment.this).inflate(R.layout.printer_name_alertdialog, null);
            final EditText printerName = view.findViewById(R.id.printername);
            try {
                printerName.setHint(printerString);
            } catch (NullPointerException e) {
                printerName.setHint("Enter printer name");
            }
            alertDialog.setView(view);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String inputString = printerName.getText().toString().trim();
                    if (inputString.isEmpty()) {
                        printerName.setError("Empty");
                    } else {
                        SharedPreferences.Editor editor = printerSharedPref.edit();
                        editor.putString(Constants.SP_PRINTER_NAME, inputString);
                        editor.commit();
                        Toast.makeText(CaptainMainFragment.this, "" +
                                "Confirmed", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }*/
        return super.onOptionsItemSelected(item);
    }
}