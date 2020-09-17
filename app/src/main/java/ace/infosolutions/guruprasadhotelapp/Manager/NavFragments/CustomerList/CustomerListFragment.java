package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.CustomerInfo;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;

public class CustomerListFragment extends Fragment {
    private static final String CUSTOMERS = "Customers";
    private static final String TABLES = "Tables";
    private static final String KOT = "KOT";
    private static final String FINAL_BILL = "FINAL_BILL";
    private static final String COST = "COST";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CustomerFirestoreAdapter adapter;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Customers");
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.managercustomerlist,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("List of current customers");
        recyclerView = view.findViewById(R.id.customerlist_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        builder = new AlertDialog.Builder(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerview();
        adapter.setOnItemClickListener(new CustomerFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String document_id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(),ConfirmFinalBill.class);
                intent.putExtra("FinalDOCID",document_id);
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new CustomerFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int pos) {
                String docId = documentSnapshot.getId();
                CustomerInfo customerInfo = documentSnapshot.toObject(CustomerInfo.class);
                String table_type = customerInfo.getTable_type();
                int table_no = customerInfo.getTable_no();
                int position = pos;
                //TODO Show alertdialog before deleting the document
                //Update Tables field
                setupAlertdialog(docId,pos,table_no,table_type);
            }
        });

    }

    private void setupAlertdialog(final String doc_id, final int pos, final int table_no, final String table_type) {
        builder.setTitle("Delete order!")
                .setIcon(R.drawable.ic_shopping_cart)
                .setMessage("Are you sure want to delete the order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDocument(doc_id,pos,table_no,table_type);
                    }
                }).setNegativeButton("Cancel",null);
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteDocument(final String doc_id, int pos, final int table_no, final String table_type) {
        db.collection(CUSTOMERS).document(doc_id).collection(KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String id = snapshot.getId();
                        db.collection(CUSTOMERS).document(doc_id).collection(KOT).document(id).delete();
                    }
                }

            }
        });
        db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        String id = snapshot.getId();
                        db.collection(CUSTOMERS).document(doc_id).collection(FINAL_BILL).document(id).delete();
                    }
                }

            }
        });
        db.collection(CUSTOMERS).document(doc_id).collection(COST).document(COST).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    db.collection(CUSTOMERS).document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      db.collection(TABLES).document(table_type).update(String.valueOf(table_no),true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful()){
                                                  Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      });
                                  }
                        }
                    });
                }

            }
        });

    }


    private void setupRecyclerview() {
        Query query = collectionReference;
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query,customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapter(cust);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
