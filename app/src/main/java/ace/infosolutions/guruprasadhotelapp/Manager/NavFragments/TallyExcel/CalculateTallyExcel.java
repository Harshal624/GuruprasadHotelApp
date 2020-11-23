package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

public class CalculateTallyExcel extends AppCompatActivity {
    private String type, date;
    private RecyclerView orderRecyclerview, parcelRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.LayoutManager layoutManager, layoutManager2;
    private CollectionReference dailyOrder, dailyParcel;
    private ArrayList<DailyOrderTallyPOJO> dailyOrderarray;
    private ArrayList<ParcelTallyPOJO> parcelTallyarray;

    private double bardining_sum = 0.0;
    private double vipdining_sum = 0.0;
    private double family_sum = 0.0;
    private double acfamily_sum = 0.0;
    private double parceltotal_sum = 0.0;
    private double discounttotal_sum = 0.0;
    private double grandtotal_sum = 0.0;
    private double onlinetotal_sum = 0.0;
    private ImageButton downloadExcel;

    private LinearLayout calc_parcel_month, calc_cash_month, calc_online_month, calc_discount_month, calc_grand_month;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tally_excel);
        orderRecyclerview = findViewById(R.id.recyclerview1);
        parcelRecyclerView = findViewById(R.id.recyclerview2);
        layoutManager = new LinearLayoutManager(this);
        layoutManager2 = new LinearLayoutManager(this);
        dailyOrderarray = new ArrayList<>();
        parcelTallyarray = new ArrayList<>();

        calc_parcel_month = findViewById(R.id.calc_parcel_month);
        calc_cash_month = findViewById(R.id.calc_cash_month);
        calc_online_month = findViewById(R.id.calc_online_month);
        calc_discount_month = findViewById(R.id.calc_discount_month);
        calc_grand_month = findViewById(R.id.calc_grand_month);

        downloadExcel = findViewById(R.id.downloadExcel);

        date = getIntent().getStringExtra("DATE");
        type = getIntent().getStringExtra("TYPE");
        setUpParcelRecyclerView();
        setUpOrderRecyclerView();
        if (type.equals("Monthly")) {
            TextView tallyTitle = findViewById(R.id.tally_title);
            tallyTitle.setText(date + "  (" + "Summary" + ")");
            TextView orderdetails = findViewById(R.id.orderdetails);
            TextView parceldetails = findViewById(R.id.parceldetails);
            ConstraintLayout order_status = findViewById(R.id.tableheader);
            ConstraintLayout order_status2 = findViewById(R.id.tableheader2);
            orderdetails.setVisibility(View.GONE);
            parceldetails.setVisibility(View.GONE);
            order_status.setVisibility(View.GONE);
            order_status2.setVisibility(View.GONE);
            parcelRecyclerView.setVisibility(View.GONE);
            orderRecyclerview.setVisibility(View.GONE);
        } else {
            TextView tallyTitle = findViewById(R.id.tally_title);
            tallyTitle.setText(date + " - " + "Summary");

        }
        setUpChildTextViews();
        downloadExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Daily")) {
                    generateExcel();
                } else {
                    generateMonthlyExcel();
                }
            }
        });
        InternetConn conn = new InternetConn(this);
        final TextView cashtotal = findViewById(R.id.cash_total);
        if (conn.haveNetworkConnection()) {
            downloadExcel.setEnabled(false);
            cashtotal.setText("Calculating...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    double ctotal = grandtotal_sum - discounttotal_sum;
                    cashtotal.setText("" + ctotal);
                    downloadExcel.setEnabled(true);
                }
            }, 3000);
        }

        calc_discount_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Monthly")) {
                    Intent intent = new Intent(getApplicationContext(), CalcTallyMonthDay.class);
                    intent.putExtra("DATEMON", date);
                    intent.putExtra("TYPE", "DISCOUNT");
                    startActivity(intent);
                }
            }
        });
        calc_cash_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Monthly")) {
                    Intent intent = new Intent(getApplicationContext(), CalcTallyMonthDay.class);
                    intent.putExtra("DATEMON", date);
                    intent.putExtra("TYPE", "cash");
                    startActivity(intent);
                }
            }
        });
        calc_grand_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Monthly")) {
                    Intent intent = new Intent(getApplicationContext(), CalcTallyMonthDay.class);
                    intent.putExtra("DATEMON", date);
                    intent.putExtra("TYPE", "GRANDTOTAL");
                    startActivity(intent);
                }
            }
        });
        calc_online_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Monthly")) {
                    Intent intent = new Intent(getApplicationContext(), CalcTallyMonthDay.class);
                    intent.putExtra("DATEMON", date);
                    intent.putExtra("TYPE", "ONLINETOTAL");
                    startActivity(intent);
                }
            }
        });
        calc_parcel_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Monthly")) {
                    Intent intent = new Intent(getApplicationContext(), CalcTallyMonthDay.class);
                    intent.putExtra("DATEMON", date);
                    intent.putExtra("TYPE", "PARCELS");
                    startActivity(intent);
                }
            }
        });


    }


    private void setUpOrderRecyclerView() {
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

    private void setUpParcelRecyclerView() {
        dailyParcel = db.collection("PARCEL_HISTORY");
        dailyParcel.whereEqualTo("date_completed", date).orderBy("time_completed", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    String bill_no = snapshot.getString("bill_no");
                    String time_completed = snapshot.getString("time_completed");
                    String payment_mode = snapshot.getString("payment_mode");
                    double discount = snapshot.getDouble("discount");
                    double subtotal = snapshot.getDouble("subtotal");
                    double total_cost = snapshot.getDouble("total_cost");
                    String cust_name = snapshot.getString("customer_name");
                    String cust_address = snapshot.getString("customer_address");
                    String cust_contact = snapshot.getString("customer_contact");
                    if (cust_address.equals("") || cust_address.equals(null)) {
                        cust_address = "NA";
                    }

                    ParcelTallyPOJO pojo = new ParcelTallyPOJO(bill_no, cust_name, cust_address, payment_mode, discount
                            , subtotal, total_cost, time_completed, cust_contact);
                    parcelTallyarray.add(pojo);

                }
                if (!parcelTallyarray.isEmpty()) {
                    ParcelTallyAdapter adapter = new ParcelTallyAdapter(parcelTallyarray);
                    parcelRecyclerView.setLayoutManager(layoutManager2);
                    parcelRecyclerView.setHasFixedSize(true);
                    parcelRecyclerView.setAdapter(adapter);
                }

            }
        });
    }

    private void setUpChildTextViews() {
        final TextView family_total = findViewById(R.id.family_total);
        final TextView ac_family_total = findViewById(R.id.ac_family_total);
        final TextView bar_dining_total = findViewById(R.id.bar_dining_total);
        final TextView vip_total = findViewById(R.id.vip_total);

        final TextView parcel_total = findViewById(R.id.parcel_total);
        final TextView discount_total = findViewById(R.id.discount_total);
        final TextView online_total = findViewById(R.id.online_total);
        final TextView grandtotal = findViewById(R.id.grandtotal);

        final DocumentReference discount_ref = db.collection("TALLY").document(type).
                collection("DISCOUNT").document(date);
        final DocumentReference grandtotal_ref = db.collection("TALLY").document(type).
                collection("GRANDTOTAL").document(date);
        final DocumentReference onlinetotal_ref = db.collection("TALLY").document(type).
                collection("ONLINETOTAL").document(date);
        final DocumentReference parcels_ref = db.collection("TALLY").document(type).
                collection("PARCELS").document(date);
        CollectionReference bardining_ref, vipdining_ref, family_ref, acfamily_ref;

        String TABLETALLY_TYPE;

        if (type.equals("Daily")) {
            TABLETALLY_TYPE = "TABLETALLYDAILY";
        } else {
            TABLETALLY_TYPE = "TABLETALLYMONTHLY";
        }
        bardining_ref = db.collection(TABLETALLY_TYPE).document(date).collection("Bar Dining");
        vipdining_ref = db.collection(TABLETALLY_TYPE).document(date).collection("VIP Dining");
        family_ref = db.collection(TABLETALLY_TYPE).document(date).collection("Family");
        acfamily_ref = db.collection(TABLETALLY_TYPE).document(date).collection("AC Family");

        bardining_ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        try {
                            bardining_sum = bardining_sum + snapshot.getDouble("tabletotal");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (bardining_sum != 0.0) {
                        //settext here
                        bar_dining_total.setText("" + bardining_sum);
                    }
                }
            }
        });
        vipdining_ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        try {
                            vipdining_sum = vipdining_sum + snapshot.getDouble("tabletotal");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (vipdining_sum != 0.0) {
                        //settext here
                        vip_total.setText("" + vipdining_sum);
                    }
                }
            }
        });
        family_ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        try {
                            family_sum = family_sum + snapshot.getDouble("tabletotal");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (family_sum != 0.0) {
                        //settext here
                        family_total.setText("" + family_sum);
                    }
                }
            }
        });
        acfamily_ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        acfamily_sum = acfamily_sum + snapshot.getDouble("tabletotal");
                    }
                    if (acfamily_sum != 0.0) {
                        //settext here
                        ac_family_total.setText("" + acfamily_sum);
                    }
                }
            }
        });

        parcels_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                try {
                    parceltotal_sum = snapshot.getDouble("parceltotal");
                    parcel_total.setText("" + parceltotal_sum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        onlinetotal_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                try {
                    onlinetotal_sum = snapshot.getDouble("onlinetotal");
                    online_total.setText("" + onlinetotal_sum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        discount_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                try {
                    discounttotal_sum = snapshot.getDouble("grandtotal");
                    discount_total.setText("" + discounttotal_sum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        grandtotal_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                try {
                    grandtotal_sum = snapshot.getDouble("grandtotal");
                    grandtotal.setText("" + grandtotal_sum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void generateExcel() {

        Workbook wb = new HSSFWorkbook();

        int row_tracker = 0;

        //Now we are creating sheet
        Sheet sheet = null;
        sheet = wb.createSheet(date);
        //Now column and row
        Row row;
        //1st row
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Order");
        //3rd row
        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Time");
        row.createCell(1).setCellValue("Table No.");
        row.createCell(2).setCellValue("Table Type");
        row.createCell(3).setCellValue("Payment");
        row.createCell(4).setCellValue("No.of cust.");
        row.createCell(5).setCellValue("Bill N.");
        row.createCell(6).setCellValue("Subtotal");
        row.createCell(7).setCellValue("Discount");
        row.createCell(8).setCellValue("Total");

        for (int i = 0; i < dailyOrderarray.size(); i++) {
            //4 row onwards
            row_tracker = row_tracker + 1;
            row = sheet.createRow(row_tracker);
            row.createCell(0).setCellValue(dailyOrderarray.get(i).getTime_completed());
            row.createCell(1).setCellValue(dailyOrderarray.get(i).getTable_no());
            row.createCell(2).setCellValue(dailyOrderarray.get(i).getTable_type());
            row.createCell(3).setCellValue(dailyOrderarray.get(i).getPayment_mode());
            row.createCell(4).setCellValue(dailyOrderarray.get(i).getNo_of_cust());
            row.createCell(5).setCellValue(dailyOrderarray.get(i).getBILL_NO());
            row.createCell(6).setCellValue(dailyOrderarray.get(i).getSubtotal());
            row.createCell(7).setCellValue(dailyOrderarray.get(i).getDiscount());
            row.createCell(8).setCellValue(dailyOrderarray.get(i).getTotal_cost());
        }

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Parcel");

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Time");
        row.createCell(1).setCellValue("Cust. Name");
        row.createCell(2).setCellValue("Address");
        row.createCell(3).setCellValue("Payment");
        row.createCell(4).setCellValue("Contact");
        row.createCell(5).setCellValue("Bill No.");
        row.createCell(6).setCellValue("Subtotal");
        row.createCell(7).setCellValue("Discount");
        row.createCell(8).setCellValue("Total");

        for (int i = 0; i < parcelTallyarray.size(); i++) {
            row_tracker = row_tracker + 1;
            row = sheet.createRow(row_tracker);
            row.createCell(0).setCellValue(parcelTallyarray.get(i).getTime_completed());
            row.createCell(1).setCellValue(parcelTallyarray.get(i).getCust_name());
            row.createCell(2).setCellValue(parcelTallyarray.get(i).getCust_address());
            row.createCell(3).setCellValue(parcelTallyarray.get(i).getPayment_mode());
            row.createCell(4).setCellValue(parcelTallyarray.get(i).getCust_contact());
            row.createCell(5).setCellValue(parcelTallyarray.get(i).getBILL_NO());
            row.createCell(6).setCellValue(parcelTallyarray.get(i).getSubtotal());
            row.createCell(7).setCellValue(parcelTallyarray.get(i).getDiscount());
            row.createCell(8).setCellValue(parcelTallyarray.get(i).getTotal_cost());
        }
        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Table Wise");
        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Family");
        row.createCell(1).setCellValue("AC Family");
        row.createCell(2).setCellValue("VIP Dining");
        row.createCell(3).setCellValue("Bar Dining");
        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue(family_sum);
        row.createCell(1).setCellValue(acfamily_sum);
        row.createCell(2).setCellValue(vipdining_sum);
        row.createCell(3).setCellValue(bardining_sum);

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Parcel Total");

        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue(parceltotal_sum);

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Onlinetotal");
        row.createCell(1).setCellValue("Cashtotal");
        row.createCell(2).setCellValue("Discounttotal");
        row.createCell(3).setCellValue("Grandtotal");

        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        double cashtotal = grandtotal_sum - onlinetotal_sum;
        row.createCell(0).setCellValue(onlinetotal_sum);
        row.createCell(1).setCellValue(cashtotal);
        row.createCell(2).setCellValue(discounttotal_sum);
        row.createCell(3).setCellValue(grandtotal_sum);



        sheet.setColumnWidth(0, (10 * 400));
        sheet.setColumnWidth(1, (10 * 500));
        sheet.setColumnWidth(2, (10 * 400));
        sheet.setColumnWidth(3, (10 * 350));
        sheet.setColumnWidth(4, (10 * 400));
        sheet.setColumnWidth(5, (10 * 400));
        sheet.setColumnWidth(6, (10 * 400));
        sheet.setColumnWidth(7, (10 * 400));
        sheet.setColumnWidth(8, (10 * 400));

        // File file = new File(getExternalFilesDir(null), date + ".xls");

        File file = getAbsoluteFile(date + ".xls", getApplicationContext());

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            //
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            // emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"Enter the email"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hotel daily summary");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Date:" + date);
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "Cannot send email", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Select option"));
            //
            Toast.makeText(getApplicationContext(), "Excel downloaded and saved to " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (Exception e2) {
            Toast.makeText(this, "Failed to save excel file", Toast.LENGTH_SHORT).show();
        }

    }

    private File getAbsoluteFile(String relativePath, Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(context.getExternalFilesDir(null), relativePath);
        } else {
            return new File(context.getFilesDir(), relativePath);
        }
    }

    private void generateMonthlyExcel() {
        Workbook wb = new HSSFWorkbook();

        int row_tracker = 0;

        //Now we are creating sheet
        Sheet sheet = null;
        sheet = wb.createSheet(date);
        //Now column and row
        Row row;
        //1st row
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Tablewise");
        //3rd row
        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Family");
        row.createCell(1).setCellValue("AC Family");
        row.createCell(2).setCellValue("VIP Total");
        row.createCell(3).setCellValue("Bar Dining");

        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue(family_sum);
        row.createCell(1).setCellValue(acfamily_sum);
        row.createCell(2).setCellValue(vipdining_sum);
        row.createCell(3).setCellValue(bardining_sum);

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Parcel Total");

        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue(parceltotal_sum);

        row_tracker = row_tracker + 2;
        row = sheet.createRow(row_tracker);
        row.createCell(0).setCellValue("Onlinetotal");
        row.createCell(1).setCellValue("Cashtotal");
        row.createCell(2).setCellValue("Discounttotal");
        row.createCell(3).setCellValue("Grandtotal");

        row_tracker = row_tracker + 1;
        row = sheet.createRow(row_tracker);
        double cashtotal = grandtotal_sum - onlinetotal_sum;
        row.createCell(0).setCellValue(onlinetotal_sum);
        row.createCell(1).setCellValue(cashtotal);
        row.createCell(2).setCellValue(discounttotal_sum);
        row.createCell(3).setCellValue(grandtotal_sum);

        sheet.setColumnWidth(0, (10 * 400));
        sheet.setColumnWidth(1, (10 * 500));
        sheet.setColumnWidth(2, (10 * 400));
        sheet.setColumnWidth(3, (10 * 350));
        sheet.setColumnWidth(4, (10 * 400));
        sheet.setColumnWidth(5, (10 * 400));
        sheet.setColumnWidth(6, (10 * 400));
        sheet.setColumnWidth(7, (10 * 400));
        sheet.setColumnWidth(8, (10 * 400));


        // File file = new File(getExternalFilesDir(null), date + ".xls");

        File file = getAbsoluteFile(date + ".xls", getApplicationContext());

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            //
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"Enter the email"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hotel monthly summary");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Month:" + date);
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "Cannot send email", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Select option"));
            //
            Toast.makeText(getApplicationContext(), "Excel downloaded and saved to " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (Exception e2) {
            Toast.makeText(this, "Failed to save excel file", Toast.LENGTH_SHORT).show();
        }


    }

}
