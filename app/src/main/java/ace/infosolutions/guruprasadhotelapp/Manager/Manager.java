package ace.infosolutions.guruprasadhotelapp.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ace.infosolutions.guruprasadhotelapp.MainActivity;
import ace.infosolutions.guruprasadhotelapp.R;

public class Manager extends AppCompatActivity {
    private ImageButton signout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        signout = (ImageButton)findViewById(R.id.signout);
        firebaseAuth = FirebaseAuth.getInstance();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerSignout();
            }
        });
    }

    private void managerSignout() {
        firebaseAuth.signOut();
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }
}
