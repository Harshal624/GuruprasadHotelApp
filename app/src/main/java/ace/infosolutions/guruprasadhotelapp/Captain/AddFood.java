package ace.infosolutions.guruprasadhotelapp.Captain;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import ace.infosolutions.guruprasadhotelapp.R;

public class AddFood extends AppCompatActivity {
    private TextView doc_id1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button delete_order;
    private static final String TABLE_COLLECTION = "Tables";
    private static final String CUSTOMER_COLLECTION = "Customers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        delete_order = (Button)findViewById(R.id.delete_order);
        doc_id1 = (TextView)findViewById(R.id.doc_id);
        final String doc_id = getIntent().getStringExtra("ID");
        final String table_type = getIntent().getStringExtra("TableType");
        final String table_no = getIntent().getStringExtra("Table_no");
        Log.e("Table type",table_type);
        Log.e("Table no",table_no);
        doc_id1.setText(doc_id);

        delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(CUSTOMER_COLLECTION).document(doc_id).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db.collection(TABLE_COLLECTION).document(table_type).update(table_no,true).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                finish();
                                                Toast.makeText(AddFood.this, "Order successfully deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                ).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddFood.this, "Failed to delete the order", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddFood.this, "Failed to delete the order", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }
}
