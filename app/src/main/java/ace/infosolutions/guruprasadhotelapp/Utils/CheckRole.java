package ace.infosolutions.guruprasadhotelapp.Utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ace.infosolutions.guruprasadhotelapp.Captain.CaptainMainFragment;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class CheckRole extends AppCompatActivity {
    private String MANAGER_UID;
    private String CAPTAIN_UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MANAGER_UID =  this.getResources().getString(R.string.MANAGER_UID);
        CAPTAIN_UID =  this.getResources().getString(R.string.CAPTAIN_UID);
        String userId = getIntent().getStringExtra("User");
        if(userId.equals(MANAGER_UID)){
            startActivity(new Intent(getApplicationContext(), Manager.class));
        }
        else if(userId.equals(CAPTAIN_UID)){
            startActivity(new Intent(getApplicationContext(), CaptainMainFragment.class));
        }
    }


}
