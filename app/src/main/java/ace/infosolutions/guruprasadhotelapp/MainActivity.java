package ace.infosolutions.guruprasadhotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import ace.infosolutions.guruprasadhotelapp.Captain.Captain;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.login);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

