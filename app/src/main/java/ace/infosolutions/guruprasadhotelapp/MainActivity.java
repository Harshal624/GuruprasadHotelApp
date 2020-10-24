package ace.infosolutions.guruprasadhotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leo.simplearcloader.SimpleArcLoader;

import ace.infosolutions.guruprasadhotelapp.Utils.CheckRole;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

public class MainActivity extends AppCompatActivity {
    String username, password;
    private Button login;
    private EditText usernameEdittext, passwordEdittext;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SimpleArcLoader progressBar;
    private InternetConn conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        conn = new InternetConn(this);
        progressBar = findViewById(R.id.loader);
        login = findViewById(R.id.login);
        usernameEdittext = findViewById(R.id.username);
        passwordEdittext = findViewById(R.id.password);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), CheckRole.class);
                    intent.putExtra("User", user.getUid());
                    startActivity(intent);
                } else {

                }
            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                username = usernameEdittext.getText().toString().trim();
                password = passwordEdittext.getText().toString().trim();
                if (username.equals("") && password.equals("")) {
                    usernameEdittext.setError("Enter email");
                    passwordEdittext.setError("Enter password");
                    progressBar.setVisibility(View.GONE);


                } else if (username.equals("") && !password.equals("")) {
                    usernameEdittext.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
                if (!username.equals("") && password.equals("")) {
                    progressBar.setVisibility(View.GONE);
                    passwordEdittext.setError("Enter password");

                } else if (!username.equals("") && !password.equals("")) {

                    if (conn.haveNetworkConnection())
                        usersignin(username, password);
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private void usersignin(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), CheckRole.class);
                            intent.putExtra("User", user.getUid());
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                        }
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
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

