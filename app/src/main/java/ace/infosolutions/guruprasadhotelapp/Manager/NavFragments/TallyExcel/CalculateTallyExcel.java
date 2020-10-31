package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class CalculateTallyExcel extends AppCompatActivity {
    private String type, date;
    private RecyclerView orderRecyclerview, parcelRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.LayoutManager layoutManager;
    private CollectionReference dailyOrder, dailyParcel, monthlyOrder, monthlyParcel;
    private ArrayList<DailyOrderTallyPOJO> dailyOrderarray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tally_excel);
        orderRecyclerview = findViewById(R.id.recyclerview1);
        parcelRecyclerView = findViewById(R.id.recyclerview2);
        layoutManager = new LinearLayoutManager(this);
        dailyOrderarray = new ArrayList<>();

        date = getIntent().getStringExtra("DATE");
        type = getIntent().getStringExtra("TYPE");

        if (type.equals("Monthly")) {
            date = date.substring(3, 7);
            TextView tallyTitle = findViewById(R.id.tally_title);
            tallyTitle.setText(date + " - " + "Summary");
        } else {
            TextView tallyTitle = findViewById(R.id.tally_title);
            tallyTitle.setText(date + " - " + "Summary");
            dailyOrder = db.collection("HISTORY");
            dailyOrder.whereEqualTo("date_completed", date).orderBy("time_completed", Query.Direction.ASCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String bill_no = snapshot.getString("bill_no");
                        String time_completed = snapshot.getString("time_completed");
                        int no_of_cust = snapshot.getDouble("no_of_cust").intValue();
                        String payment_mode = snapshot.getString("payment_mode");
                        int table_no = snapshot.getDouble("table_no").intValue();
                        String table_type = snapshot.getString("table_type");
                        double discount = snapshot.getDouble("discount");
                        double subtotal = snapshot.getDouble("subtotal");
                        double total_cost = snapshot.getDouble("total_cost");

                        DailyOrderTallyPOJO pojo = new DailyOrderTallyPOJO(bill_no, no_of_cust, payment_mode, table_no, table_type,
                                discount, subtotal, total_cost, time_completed);
                        dailyOrderarray.add(pojo);
                    }
                    if (!dailyOrderarray.isEmpty()) {
                        DailyOrderAdapter adapter = new DailyOrderAdapter(dailyOrderarray);
                        orderRecyclerview.setLayoutManager(layoutManager);
                        orderRecyclerview.setHasFixedSize(true);
                        orderRecyclerview.setAdapter(adapter);
                    }
                }
            });
        }


    }
}
