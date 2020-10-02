package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ace.infosolutions.guruprasadhotelapp.R;

public class TableTally extends AppCompatActivity {

    private Intent intent;
    private LinearLayout family,ac_family,vip_dining,bar_dining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_tally);

        family = findViewById(R.id.linearLayout9);
        ac_family = findViewById(R.id.linearLayout10);
        vip_dining = findViewById(R.id.linearLayout11);
        bar_dining = findViewById(R.id.bardining);

        boolean isdaily = getIntent().getBooleanExtra("ISDAILY",false);
        String doc_id = getIntent().getStringExtra("DOCID");

         intent = new Intent(getApplicationContext(), CalculateTallyTable.class);
         intent.putExtra("DOCID",doc_id);
         intent.putExtra("ISDAILY",isdaily);

         family.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 intent.putExtra("TYPE","Family");
                 startActivity(intent);
             }
         });
        ac_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("TYPE","AC Family");
                startActivity(intent);
            }
        });

        vip_dining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("TYPE","VIP Dining");
                startActivity(intent);
            }
        });

        bar_dining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("TYPE","Bar Dining");
                startActivity(intent);
            }
        });
    }

}
