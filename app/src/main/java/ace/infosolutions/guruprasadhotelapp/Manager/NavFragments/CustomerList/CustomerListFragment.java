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
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String TABLES = "Tables";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CustomerFirestoreAdapter adapter;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(CUSTOMERS);
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
                //Update Tables field
                setupAlertdialog(docId,pos,table_no,table_type);
            }
        });

    }

    private void setupAlertdialog(final String doc_id, final int pos, final int table_no, final String table_type) {
        builder.setTitle("Delete order!")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure want to delete the order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO DELETE HERE
                    }
                }).setNegativeButton("Cancel",null);
        alertDialog = builder.create();
        alertDialog.show();

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
