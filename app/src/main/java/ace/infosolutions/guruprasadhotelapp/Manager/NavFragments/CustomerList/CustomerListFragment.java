package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.CustomerInfo;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

import static ace.infosolutions.guruprasadhotelapp.Captain.AddCustomer.TABLES;

public class CustomerListFragment extends Fragment {
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CustomerFirestoreAdapterManager adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(CUSTOMERS);
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.managercustomerlist, container, false);
        ((Manager) getActivity()).toolbar.setTitle("Customers");
        recyclerView = view.findViewById(R.id.customerlist_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        builder = new AlertDialog.Builder(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerview();
        adapter.setOnItemClickListener(new CustomerFirestoreAdapterManager.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String document_id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ConfirmFinalBill.class);
                intent.putExtra("FinalDOCID", document_id);
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new CustomerFirestoreAdapterManager.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int pos) {
                CustomerInfo customerInfo = documentSnapshot.toObject(CustomerInfo.class);
                //Update Tables field
                setupAlertdialog(documentSnapshot, customerInfo);
            }
        });

    }


    private void setupAlertdialog(final DocumentSnapshot snapshot, final CustomerInfo customerInfo) {
        builder.setTitle("Delete order!")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure want to delete the order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCustomer(snapshot, customerInfo);
                    }
                }).setNegativeButton("Cancel", null);
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteCustomer(final DocumentSnapshot snapshot, final CustomerInfo customerInfo) {
        final String doc_id = snapshot.getId();
        final DocumentReference TableRef = db.collection(TABLES).document(customerInfo.getTable_type());
        final String table_no = String.valueOf(customerInfo.getTable_no());
        final DocumentReference parentRef = collectionReference.document(doc_id);
        collectionReference.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    InternetConn conn = new InternetConn(getContext());
                    if (conn.haveNetworkConnection()) {
                        double current_cost = task.getResult().getDouble("current_cost");
                        double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                        if (current_cost == 0.0 && confirmed_cost != 0.0) {
                            //delete confirmed_kot collection with parentdocument
                            deleteConfirmedKot();
                        } else if (current_cost != 0.0 && confirmed_cost != 0.0) {
                            //delete confirmed and current kot with parentdoc
                            deleteBothKot();
                        } else if (current_cost != 0.0 && confirmed_cost == 0.0) {
                            //delete current_kot coll with parent
                            deleteCurrentKot();
                        } else {
                            //delete parent doc only
                            deleteParentKot();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to delete the order, Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            private void deleteParentKot() {
                final WriteBatch batch = db.batch();
                batch.delete(parentRef);
                batch.update(TableRef, table_no, true);
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void deleteCurrentKot() {
                final WriteBatch batch = db.batch();
                parentRef.collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                //delete confirmed_kot collection documents
                                batch.delete(parentRef.collection(CURRENT_KOT).document(snapshot1.getId()));
                            }
                            //delete parent document
                            batch.delete(parentRef);
                            batch.update(TableRef, table_no, true);
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

            private void deleteBothKot() {
                final WriteBatch batch = db.batch();
                parentRef.collection(CONFIRMED_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                //delete confirmed_kot collection documents
                                batch.delete(parentRef.collection(CONFIRMED_KOT).document(snapshot1.getId()));
                            }
                            //delete current kot collection documents
                            parentRef.collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                            //delete confirmed_kot collection documents
                                            batch.delete(parentRef.collection(CURRENT_KOT).document(snapshot1.getId()));
                                        }
                                        //delete parent document
                                        batch.delete(parentRef);
                                        batch.update(TableRef, table_no, true);
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
                    }
                });


            }

            private void deleteConfirmedKot() {
                final WriteBatch batch = db.batch();
                parentRef.collection(CONFIRMED_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                //delete confirmed_kot collection documents
                                batch.delete(parentRef.collection(CONFIRMED_KOT).document(snapshot1.getId()));
                            }
                            //delete parent document
                            batch.delete(parentRef);
                            batch.update(TableRef, table_no, true);
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
        });
    }


    private void setupRecyclerview() {
        Query query = collectionReference.orderBy("time_arrived", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query, customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapterManager(cust, getView());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
