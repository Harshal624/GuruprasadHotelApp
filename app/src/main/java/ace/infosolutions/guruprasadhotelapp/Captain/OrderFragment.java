package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

public class OrderFragment extends Fragment {
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String TABLE_COLLECTION = "Tables";
    private static final String CUSTOMER_COLLECTION = "CUSTOMERS";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private FloatingActionButton add_customer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(CUSTOMER_COLLECTION);
    private CustomerFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AlertDialog alertDialog;
    private androidx.appcompat.app.AlertDialog.Builder builder;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private InternetConn internetConn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        add_customer = view.findViewById(R.id.customerAdd);
        recyclerView = view.findViewById(R.id.orderRecyclerview);
        builder = new AlertDialog.Builder(getContext());
        internetConn = new InternetConn(getContext());
        progressBar = view.findViewById(R.id.orderfrag_progressbar);
        sharedPreferences = getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getContext());
        setupReyclerview();
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internetConn.haveNetworkConnection()) {
                    startActivity(new Intent(getContext(), AddCustomer.class));
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setOnItemClickListener(new CustomerFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String docid = documentSnapshot.getId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(DOC_ID_KEY, docid);
                editor.commit();
                Intent i = new Intent(getContext(), FoodMenu.class);
                startActivity(i);
            }
        });

        adapter.setOnItemLongClickListener(new CustomerFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final DocumentSnapshot documentSnapshot, final int pos) {
                final InternetConn conn = new InternetConn(getContext());
                builder.setTitle("Delete order!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (conn.haveNetworkConnection()) {
                                    customerclass customerclass = documentSnapshot.toObject(ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass.class);
                                    final String doc_id = documentSnapshot.getId();
                                    final String table_no = String.valueOf(customerclass.getTable_no());
                                    String table_type = customerclass.getTable_type();
                                    progressBar.setVisibility(View.VISIBLE);
                                    add_customer.setEnabled(false);
                                    checkCosts(doc_id, table_no, pos, table_type);

                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setMessage("Are you sure want to delete the order?")
                        .setIcon(R.drawable.ic_delete);
                alertDialog = builder.create();
                alertDialog.show();


            }
        });
    }

    private void checkCosts(final String doc_id, final String table_no, final int pos, final String table_type) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                    double current_cost = task.getResult().getDouble("current_cost");
                    if (confirmed_cost == 0 && current_cost != 0) {
                        deleteCurrent_kot(doc_id, table_no, pos, table_type);
                        deleteParentDoc(doc_id, table_no, pos, table_type);
                    } else if (confirmed_cost == 0 && current_cost == 0) {
                        deleteParentDoc(doc_id, table_no, pos, table_type);
                    } else {
                        alertDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        add_customer.setEnabled(true);
                        Toast.makeText(getContext(), "Cannot delete order", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteCurrent_kot(final String doc_id, final String table_no, final int pos, final String table_type) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String current_kot_id = snapshot.getId();
                        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(CURRENT_KOT).document(current_kot_id)
                                .delete();
                    }

                }
            }
        });
    }

    private void deleteParentDoc(String doc_id, final String table_no, final int pos, final String table_type) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateTableStatus(table_no, table_type, pos);
                }
            }
        });
    }

    private void updateTableStatus(String table_no, String table_type, final int pos) {
        Map<String, Object> table_update = new HashMap<>();
        table_update.put(table_no, true);
        db.collection(TABLE_COLLECTION).document(table_type).update(table_update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                adapter.notifyItemRemoved(pos);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                add_customer.setEnabled(true);
            }
        });
    }

    private void setupReyclerview() {
        Query query = collectionReference;
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query, customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapter(cust, getView());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
