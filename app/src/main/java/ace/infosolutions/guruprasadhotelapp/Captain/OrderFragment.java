package ace.infosolutions.guruprasadhotelapp.Captain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.order_fragment,container,false);
        add_customer = (FloatingActionButton)view.findViewById(R.id.customerAdd);
        recyclerView= view.findViewById(R.id.orderRecyclerview);
        builder = new AlertDialog.Builder(getContext());
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
                //TODO start the activity only if network is available
                if(haveNetworkConnection()){
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
                customerclass customerclass = documentSnapshot.toObject(customerclass.class);
                String id = documentSnapshot.getId();
                String table_type = customerclass.getTable_type();
                String table_no=String.valueOf(customerclass.getTable_no());
                Intent i = new Intent(getContext(),FoodMenu.class);
                i.putExtra("ID",id);
                i.putExtra("Table_type",table_type);
                i.putExtra("Table_no",table_no);
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
                                customerclass customerclass = documentSnapshot.toObject(ace.infosolutions.guruprasadhotelapp.Captain.customerclass.class);
                                final String id = customerclass.getTable_type();
                                final String doc_id = documentSnapshot.getId();
                                final String table_no = String.valueOf(customerclass.getTable_no());

                                db.collection(CUSTOMER_COLLECTION).document(doc_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection(TABLE_COLLECTION).document(id).update(table_no,true).addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Order successfully deleted", Toast.LENGTH_SHORT).show();
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
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


}
