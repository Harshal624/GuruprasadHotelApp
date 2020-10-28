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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

import static ace.infosolutions.guruprasadhotelapp.Captain.AddCustomer.CUSTOMERS;
import static ace.infosolutions.guruprasadhotelapp.Captain.AddCustomer.TABLES;
import static ace.infosolutions.guruprasadhotelapp.Captain.ItemList.CURRENT_KOT;

public class OrderFragment extends Fragment {
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String TABLE_COLLECTION = "Tables";
    private FloatingActionButton add_customer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(CUSTOMERS);
    private CustomerFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AlertDialog alertDialog;
    private androidx.appcompat.app.AlertDialog.Builder builder;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private InternetConn internetConn;
    private double current_cost, confirmed_cost;


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
                                    final customerclass customerclass = documentSnapshot.toObject(ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass.class);
                                    final String doc_id = documentSnapshot.getId();
                                    final String tableNo = String.valueOf(customerclass.getTable_no());
                                    // progressBar.setVisibility(View.VISIBLE);
                                    // add_customer.setEnabled(false);
                                    final DocumentReference custRef = db.collection(CUSTOMERS).document(doc_id);
                                    final DocumentReference tableRef = db.collection(TABLES).document(customerclass.getTable_type());

                                    custRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot snapshot) {
                                            current_cost = snapshot.getDouble("current_cost");
                                            confirmed_cost = snapshot.getDouble("confirmed_cost");
                                            if (current_cost != 0.0 && confirmed_cost != 0.0) {
                                                //delete current,confirmed_kot and parent
                                                Toast.makeText(getContext(), "Cannot delete the order", Toast.LENGTH_SHORT).show();
                                            } else if (current_cost == 0.0 && confirmed_cost == 0.0) {
                                                //delete parent only
                                                deleteParentDoc();
                                            } else if (current_cost != 0.0 && confirmed_cost == 0.0) {
                                                //delete current_kot and parent
                                                deleteCurrentandParent();

                                            } else if (current_cost == 0.0 && confirmed_cost != 0.0) {
                                                //delete confirmed_kot and parent
                                                Toast.makeText(getContext(), "Cannot delete the order", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        private void deleteCurrentandParent() {
                                            final WriteBatch batch = db.batch();
                                            custRef.collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                            //deleting current_kot collection contents
                                                            batch.delete(custRef.collection(CURRENT_KOT).document(snapshot.getId()));
                                                        }
                                                        //deleting parent doc
                                                        batch.delete(custRef);
                                                        //updating table no
                                                        batch.update(tableRef, tableNo, true);
                                                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }

                                        private void deleteParentDoc() {
                                            //delete document and update the table
                                            WriteBatch batch = db.batch();
                                            batch.delete(custRef);
                                            batch.update(tableRef, tableNo, true);
                                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                    });
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

    private void setupReyclerview() {
        Query query = collectionReference.orderBy("time_arrived", Query.Direction.DESCENDING);
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

}
