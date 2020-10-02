package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String TABLES = "Tables";
    private static final String CURRENT_KOT ="CURRENT_KOT" ;
    private static final String CONFIRMED_KOT ="CONFIRMED_KOT" ;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CustomerFirestoreAdapter adapter;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(CUSTOMERS);
    private AlertDialog alertDialog,pinAlert;
    private View pinView;
    private AlertDialog.Builder builder;

    private ImageButton confirmpin, cancelpin;
    private EditText enter_pin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.managercustomerlist,container,false);
        ((Manager) getActivity() ).toolbar.setTitle("List of current customers");
        recyclerView = view.findViewById(R.id.customerlist_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        builder = new AlertDialog.Builder(getContext());
        pinView = inflater.inflate(R.layout.pin_alertdialog,null);
        pinAlert = builder.create();
        confirmpin = pinView.findViewById(R.id.confirmpin);
        cancelpin = pinView.findViewById(R.id.cancelpin);
        enter_pin = pinView.findViewById(R.id.enter_pin);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerview();
        adapter.setOnItemClickListener(new CustomerFirestoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                setUpPinAlert(documentSnapshot);
                pinAlert.setView(pinView);
               pinAlert.setCancelable(true);
                pinAlert.show();

            }
        });

        adapter.setOnItemLongClickListener(new CustomerFirestoreAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int pos) {
                CustomerInfo customerInfo = documentSnapshot.toObject(CustomerInfo.class);
                //Update Tables field
                setupAlertdialog(documentSnapshot,customerInfo);
            }
        });

    }

    private void setUpPinAlert(final DocumentSnapshot documentSnapshot) {
        enter_pin.setText("");
        enter_pin.requestFocus();
        enter_pin.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(enter_pin, 0);
            }
        }, 100);
        confirmpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_pin.getText().toString().trim().equals("") || enter_pin.getText().toString().trim().equals(null)) {
                    enter_pin.setError("Enter manager pin");
                } else {
                    int input_pin = Integer.parseInt(enter_pin.getText().toString().trim());
                    verifyPin(input_pin);
                }
            }

            private void verifyPin(final int input_pin) {
                db.collection("MANAGERPIN").document("PIN").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            int manager_pin = task.getResult().getDouble("pin").intValue();
                            if (input_pin == manager_pin) {
                                pinAlert.dismiss();
                                String document_id = documentSnapshot.getId();
                                Intent intent = new Intent(getContext(), ConfirmFinalBill.class);
                                intent.putExtra("FinalDOCID", document_id);
                                startActivity(intent);

                            } else {
                                enter_pin.setError("Wrong Pin");
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to verify pin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        cancelpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinAlert.dismiss();
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
                        deleteCustomer(snapshot,customerInfo);
                    }
                }).setNegativeButton("Cancel",null);
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteCustomer(final DocumentSnapshot snapshot, final CustomerInfo customerInfo) {
        String doc_id = snapshot.getId();
        collectionReference.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    double current_cost = task.getResult().getDouble("current_cost");
                    double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                    if(current_cost == 0.0 && confirmed_cost != 0.0){
                        //delete confirmed_kot collection with parentdocument
                        deleteConfirmedKOT(snapshot);
                        deleteParentDoc(snapshot,customerInfo);

                    }
                    else if(current_cost != 0.0 && confirmed_cost != 0.0){
                        //delete confirmed and current kot with parentdoc
                        deleteConfirmedKOT(snapshot);
                        deleteCurrentKOT(snapshot);
                        deleteParentDoc(snapshot,customerInfo);
                    }
                    else if(current_cost != 0.0 && confirmed_cost == 0.0){
                        //delete current_kot coll with parent
                        deleteCurrentKOT(snapshot);
                        deleteParentDoc(snapshot,customerInfo);
                    }
                    else{
                        deleteParentDoc(snapshot,customerInfo);
                    }

                }
                else{

                }
            }
        });
    }

    private void deleteCurrentKOT(DocumentSnapshot snapshot) {
        final String doc_id = snapshot.getId();
        collectionReference.document(doc_id).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot1: task.getResult()){
                        collectionReference.document(doc_id).collection(CURRENT_KOT).
                                document(snapshot1.getId()).delete();
                    }
                }
                else{

                }
            }
        });
    }

    private void deleteConfirmedKOT(DocumentSnapshot snapshot) {
        final String doc_id = snapshot.getId();
        collectionReference.document(doc_id).collection(CONFIRMED_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot1: task.getResult()){
                        collectionReference.document(doc_id).collection(CONFIRMED_KOT).
                                document(snapshot1.getId()).delete();
                    }
                }
                else{

                }
            }
        });
    }

    private void deleteParentDoc(DocumentSnapshot snapshot, final CustomerInfo customerInfo) {
        //update table status too
        String doc_id = snapshot.getId();
        collectionReference.document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateTableStatus(customerInfo);
                }
                else{

                }
            }
        });
    }

    private void updateTableStatus(CustomerInfo customerInfo) {
        String table_type = customerInfo.getTable_type();
        String table_no = String.valueOf(customerInfo.getTable_no());
        db.collection("Tables").document(table_type).update(table_no,true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
                else{

                }
            }
        });
    }


    private void setupRecyclerview() {
        Query query = collectionReference;
        FirestoreRecyclerOptions<customerclass> cust = new FirestoreRecyclerOptions.Builder<customerclass>()
                .setQuery(query,customerclass.class)
                .build();
        adapter = new CustomerFirestoreAdapter(cust,getView());
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
