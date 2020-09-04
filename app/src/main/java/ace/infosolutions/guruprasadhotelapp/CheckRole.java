package ace.infosolutions.guruprasadhotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import ace.infosolutions.guruprasadhotelapp.Captain.CaptainMainFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;

public class CheckRole extends AppCompatActivity {
    private static final String MANAGER_UID="fQklFOKhkmRI0oCoKy4WY1sWeEp1";
    private static final String CAPTAIN_UID="Agmw1Q6RHtT1hgETt8I29n6SSyy2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String userId = getIntent().getStringExtra("User");
        if(userId.equals(MANAGER_UID)){
            startActivity(new Intent(getApplicationContext(), Manager.class));
        }
        else if(userId.equals(CAPTAIN_UID)){
            startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
        }
        Log.e("User",userId);
    }


}
