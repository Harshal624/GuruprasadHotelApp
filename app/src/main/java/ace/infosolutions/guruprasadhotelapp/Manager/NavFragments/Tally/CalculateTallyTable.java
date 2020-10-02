package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.Tally;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TABLETALLYDAILY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TABLETALLYMONTHLY;

public class CalculateTallyTable extends AppCompatActivity {
    private TextView table_tally_type;
    String toolbar_title;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref;
    String doc_id, table_type;
    private ArrayList<DataEntry> tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tally_table);
        table_tally_type = findViewById(R.id.table_tally_type);
        boolean isdaily = getIntent().getBooleanExtra("ISDAILY",false);
        doc_id = getIntent().getStringExtra("DOCID");
        table_type = getIntent().getStringExtra("TYPE");
        tables = new ArrayList<>();

        if(isdaily){
            toolbar_title = table_type+"(Daily)";
            ref = db.collection(TABLETALLYDAILY);
        } else{
            toolbar_title = table_type+"(Monthly)";
            ref = db.collection(TABLETALLYMONTHLY);
        }
        table_tally_type.setText(toolbar_title);

        fetchTableData();

    }

    private void fetchTableData() {

        ref.document(doc_id).collection(table_type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String table_no = String.valueOf(snapshot.getDouble("table_no").intValue());
                        double table_total = snapshot.getDouble("tabletotal");
                        tables.add(new ValueDataEntry("Table No: " + table_no, table_total));
                    }
                    if (!tables.isEmpty()) {
                        setupanychart();
                    }
                }
            }
        });
    }

    private void setupanychart() {
        AnyChartView anyChartView;
        anyChartView = findViewById(R.id.anychart);
        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(CalculateTallyTable.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        pie.data(tables);
        pie.title("Tablewise revenue on " + doc_id);
        pie.labels().position("outside");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        anyChartView.setChart(pie);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
