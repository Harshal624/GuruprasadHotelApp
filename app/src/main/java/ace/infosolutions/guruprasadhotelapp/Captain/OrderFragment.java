package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.CustomerFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.InternetConn;
import ace.infosolutions.guruprasadhotelapp.R;

public class OrderFragment extends Fragment {
    private FloatingActionButton add_customer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Customers");
    private CustomerFirestoreAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private static final String TABLE_COLLECTION = "Tables";
    private static final String CUSTOMER_COLLECTION = "Customers";
    private AlertDialog alertDialog;
    private androidx.appcompat.app.AlertDialog.Builder builder;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String TABLE_TYPE_KEY = "TABLE_TYPE_KEY";
    private static final String TABLE_NO_KEY = "TABLE_NO_KEY";
    private SharedPreferences sharedPreferences;
    private final String FINAL_BILL = "FINAL_BILL";
    private final String KOT = "KOT";
    private final String COST = "COST";
    private InternetConn internetConn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.order_fragment,container,false);
        add_customer = (FloatingActionButton)view.findViewById(R.id.customerAdd);
        recyclerView= view.findViewById(R.id.orderRecyclerview);
        builder = new AlertDialog.Builder(getContext());
        internetConn = new InternetConn(getContext());
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
                if(internetConn.haveNetworkConnection()){
                    startActivity(new Intent(getContext(),AddCustomer.class));
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setOnItemClickListener(new CustomerFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //TODO ALSO TABLE_TYPE AND TABLE NO IN SHAREDPREFERENCES
                customerclass customerclass = documentSnapshot.toObject(customerclass.class);
                String table_type = customerclass.getTable_type();
                int table_no = customerclass.getTable_no();
                String docid = documentSnapshot.getId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(DOC_ID_KEY,docid);
                editor.putString(TABLE_TYPE_KEY,table_type);
                editor.putInt(TABLE_NO_KEY,table_no);
                editor.commit();
                Intent i = new Intent(getContext(),FoodMenu.class);
                startActivity(i);
            }
        });

        adapter.setOnItemLongClickListener(new CustomerFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final DocumentSnapshot documentSnapshot,final int pos) {
                builder.setTitle("Delete order!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO IF PARENT DOCUMENT COST IS ZERO, CAPTAIN CAN DELETE THE ORDER, ELSE HE CANNOT
                                customerclass customerclass = documentSnapshot.toObject(ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass.class);
                                final String id = customerclass.getTable_type();
                                final String doc_id = documentSnapshot.getId();
                                final String table_no = String.valueOf(customerclass.getTable_no());
                                checkparentcost(id,doc_id,table_no,pos);


                            }

                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setMessage("Are you sure want to delete the order?");
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void checkparentcost(final String id, final String doc_id, final String table_no, final int pos) {

        db.collection(CUSTOMER_COLLECTION).document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = snapshot.getDouble("cost");
                    if(cost == 0){
                        checkanddeleteOrder(id,doc_id,table_no,pos);
                    }
                    else{
                        alertDialog.dismiss();
                        Toast.makeText(getContext(), "Cannot delete order", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void checkanddeleteOrder(final String id, final String doc_id, final String table_no, final int pos) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(COST).document(COST)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double cost = 0;
                try {
                    cost = documentSnapshot.getDouble("cost");
                    if(cost == 0.0){
                        deleteCOSTSubcollection(id,doc_id,table_no,pos);
                    }
                    else if(cost != 0.0){
                        deleteFinalBill(id,doc_id,table_no,pos);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);

            }
        });

    }

    private void deleteFinalBill(final String id, final String doc_id, final String table_no, final int pos) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(FINAL_BILL).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //TODO INCOMPLETE DELETE OPERATION
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                final String idDoc = queryDocumentSnapshot.getId();
                                db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(FINAL_BILL)
                                        .document(idDoc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                    }
                                });
                            }
                            deleteKOTSubCollection(id,doc_id,table_no,pos);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to delete the order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteKOTSubCollection(final String id, final String doc_id, final String table_no, final int pos) {

        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(KOT).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                final String idDoc = queryDocumentSnapshot.getId();
                                db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(KOT)
                                        .document(idDoc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                    }
                                });
                            }
                            deleteCOSTSubcollection(id,doc_id,table_no,pos);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to delete the order", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteCOSTSubcollection(final String id, final String doc_id, final String table_no, final int pos) {
        db.collection(CUSTOMER_COLLECTION).document(doc_id).collection(COST).document(COST)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteParentDocument(id,doc_id,table_no,pos);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteParentDocument(final String id, String doc_id, final String table_no, final int pos) {
          db.collection(CUSTOMER_COLLECTION).document(doc_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection(TABLE_COLLECTION).document(id).update(table_no,true).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getContext(), "Order successfully deleted", Toast.LENGTH_SHORT).show();
                                adapter.notifyItemRemoved(pos);
                                adapter.notifyDataSetChanged();
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete the order", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void setupReyclerview() {
        Query query = collectionReference;
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query,customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapter(cust);
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
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
