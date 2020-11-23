package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ace.infosolutions.guruprasadhotelapp.R;

public class CalcTallyMonthDay extends AppCompatActivity {

    private String MONTH, TYPE;
    // private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_tally_month_day);

        MONTH = getIntent().getStringExtra("DATEMON");
        TYPE = getIntent().getStringExtra("TYPE");
        TextView tv = findViewById(R.id.stat);

        //a field named month should be added to the tally


        //db.collection(TALLY).document(MONTHLY).collection(TYPE).whereGreaterThanOrEqualTo("date",MONTH).


    }
}
