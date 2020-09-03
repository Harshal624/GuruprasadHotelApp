package ace.infosolutions.guruprasadhotelapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ace.infosolutions.guruprasadhotelapp.Manager.CheckRole;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText usernameEdittext,passwordEdittext;
    private FirebaseAuth firebaseAuth;
    String username,password;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView alertemail;
    private TextView alertpass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login);
        usernameEdittext = (EditText)findViewById(R.id.username);
        passwordEdittext = (EditText)findViewById(R.id.password);
        alertemail= (TextView)findViewById(R.id.alertemail);
        alertpass= (TextView)findViewById(R.id.alertpass);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Intent intent=  new Intent(getApplicationContext(),CheckRole.class);
                    intent.putExtra("User",user.getUid());
                    startActivity(intent);
                }
                else{

                }
            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEdittext.getText().toString().trim();
                password = passwordEdittext.getText().toString().trim();
                if(username.equals("") && password.equals("")){
                    alertemail.setVisibility(View.VISIBLE);
                    alertpass.setVisibility(View.VISIBLE);
                    usernameEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                    passwordEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                }
                else if(username.equals("") && !password.equals("")){
                    alertemail.setVisibility(View.VISIBLE);
                    alertpass.setVisibility(View.GONE);
                    usernameEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                    passwordEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                }
                if(!username.equals("") && password.equals("")){
                    alertemail.setVisibility(View.GONE);
                    alertpass.setVisibility(View.VISIBLE);
                    usernameEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                    passwordEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                }
                else if(!username.equals("") && !password.equals("")){
                    alertemail.setVisibility(View.GONE);
                    alertpass.setVisibility(View.GONE);
                    usernameEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                    passwordEdittext.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                    usersignin(username,password);
                }
            }

        });
    }

    private void usersignin(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent=  new Intent(getApplicationContext(),CheckRole.class);
                            intent.putExtra("User",user.getUid());
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

