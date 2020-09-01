package ace.infosolutions.guruprasadhotelapp.Captain;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import ace.infosolutions.guruprasadhotelapp.R;

public class AddCustomer extends AppCompatActivity {
    private Spinner table_type;
    private String selected_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        table_type = (Spinner)findViewById(R.id.table_type);

        ArrayAdapter<CharSequence> tabletypeadapter = ArrayAdapter.createFromResource(AddCustomer.this,R.array.Tabletype,android.R.layout.simple_spinner_item);
        tabletypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        table_type.setAdapter(tabletypeadapter);

        table_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_table =adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_table = "Family";
            }
        });


    }
}
