package ace.infosolutions.guruprasadhotelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

import ace.infosolutions.guruprasadhotelapp.Captain.Captain;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;

public class MainActivity extends AppCompatActivity {
    private EditText email,password;
    private Button login;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String email1 = email.getText().toString().trim();
                //String password1 = password.getText().toString().trim();
                //startActivity(new Intent(getApplicationContext(), Captain.class));
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id);
                String check = radioButton.getText().toString();
                if(check.equals("Captain")){
                    startActivity(new Intent(getApplicationContext(), Captain.class));
                }
                else if(check.equals("Manager")){
                    startActivity(new Intent(getApplicationContext(), Manager.class));
                }




            }
        });
    }


}

