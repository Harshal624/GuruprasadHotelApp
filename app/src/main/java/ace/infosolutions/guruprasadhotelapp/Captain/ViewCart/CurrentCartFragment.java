package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.FoodMenu;
import ace.infosolutions.guruprasadhotelapp.R;

//TODO CHANGE TO CODE OF GETTING COST FROM COST SUBCOLLECTION TO TEXTVIEW

public class CurrentCartFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewCartFirestoreAdapter adapter;
    private CollectionReference collectionReference = db.collection("Customers");
    private TextView total_cost_order;
    private ImageButton sendkotreq;
    double sum = 0;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private SharedPreferences sharedPreferences;

    private String DOC_ID = "";
    private final String CUSTOMER = "Customers";
    private final String KOT = "KOT";
    private final String FINAL_BILL = "FINAL_BILL";
    private final String COST = "COST";
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Map<String,Object> isrequested_map;
    private Map<String,Object> update_parentKOT;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentcart_fragment,container,false);
        recyclerView = view.findViewById(R.id.currentcart_recycler);
        layoutManager = new LinearLayoutManager(view.getContext());
        sendkotreq = (ImageButton) view.findViewById(R.id.sendkotreq);
        total_cost_order = view.findViewById(R.id.total_cost_order);
        isrequested_map = new HashMap<>();
        isrequested_map.put("isrequested",true);
        update_parentKOT = new HashMap<>();
        update_parentKOT.put("kotrequested",true);
        builder = new AlertDialog.Builder(getContext());
        sharedPreferences= getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY,"");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerview();
        setTotalCost();

        sendkotreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestKOT();
            }
        });

        adapter.setOnItemCartClickListener(new ViewCartFirestoreAdapter.OnItemClickListenerCart() {
            @Override
            public void onItemClickCart(DocumentSnapshot documentSnapshot, final int position) {
                final String id = documentSnapshot.getId();
                ViewCartPOJO cartPOJO = documentSnapshot.toObject(ViewCartPOJO.class);
                final String foodTitle = cartPOJO.getItem_title();
                final int foodQty = cartPOJO.getItem_qty();
                final double cost = documentSnapshot.getDouble("item_cost");

                //TODO Update the FINAL_BILL Collection
                builder.setTitle("Delete current item!")
                        .setIcon(R.drawable.ic_delete)
                        .setMessage("Are you sure want to delete the item?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                collectionReference.document(DOC_ID)
                                        .collection(KOT).document(id)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updateCost(cost,position,foodTitle,foodQty);

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Cannot delete order", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

        private void requestKOT() {
            //TODO SET isrequested field to true

            db.collection(CUSTOMER).document(DOC_ID).collection(KOT).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for(QueryDocumentSnapshot snapshot:task.getResult()){
                                    String KOT_doc_id = snapshot.getId();
                                    db.collection(CUSTOMER).document(DOC_ID).collection(KOT).document(KOT_doc_id)
                                            .update(isrequested_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed to request KOT", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                updateFinalBillisrequested();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Cannot request KOT for the given items", Toast.LENGTH_SHORT).show();

                }
            });
        }

    private void updateFinalBillisrequested() {
        collectionReference.document(DOC_ID)
                .collection(FINAL_BILL).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                QuerySnapshot querySnapshot = task.getResult();
                for(QueryDocumentSnapshot snapshot: querySnapshot) {
                    String id = snapshot.getId();
                    collectionReference.document(DOC_ID).collection(FINAL_BILL)
                            .document(id).update(isrequested_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to send KOT request", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                    updateparentdocKOT();
                }

            }
        });
    }


    private void updateparentdocKOT() {
            db.collection(CUSTOMER).document(DOC_ID).update(update_parentKOT).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "KOT Request sent successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Unable to generate KOT", Toast.LENGTH_SHORT).show();
                }
            });

        }


    private void updateFinalBill(String foodTitle, int foodQty, final int position) {
        collectionReference.document(DOC_ID)
                .collection(FINAL_BILL).whereEqualTo("item_title",foodTitle).whereEqualTo("item_qty",foodQty)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //TODO INCOMPLETE CODE
                QuerySnapshot querySnapshot = task.getResult();
                for(QueryDocumentSnapshot snapshot: querySnapshot){
                    String id = snapshot.getId();
                    collectionReference.document(DOC_ID).collection(FINAL_BILL)
                            .document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            adapter.notifyItemRemoved(position);
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Cannot delete item", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    private void setTotalCost() {

        db.collection(CUSTOMER).document(DOC_ID)
                .collection(COST)
                .document(COST).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double cost = documentSnapshot.getDouble("cost");
                if(cost == 0){
                    total_cost_order.setText("Cart is empty");
                }
                else{
                    total_cost_order.setText("Total Cost: Rs."+String.valueOf(cost));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Failed to retrieve cost", Toast.LENGTH_SHORT).show();            }
        });
    }

    private void updateCost(final double item_cost, final int position, final String foodTitle, final int foodQty) {
        db.collection(CUSTOMER).document(DOC_ID)
                .collection(COST)
                .document(COST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = snapshot.getDouble("cost");
                    cost = cost - item_cost;
                    Map<String,Double> map = new HashMap<>();
                    map.put("cost",cost);
                    total_cost_order.setText("Total Cost: Rs."+cost);
                    db.collection(CUSTOMER).document(DOC_ID)
                            .collection(COST)
                            .document(COST).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateFinalBill(foodTitle,foodQty,position);
                        }
                    });
                }

            }
        });
    }

    private void setupRecyclerview() {
        Query query = collectionReference.document(DOC_ID).collection(KOT);
        FirestoreRecyclerOptions<ViewCartPOJO> viewcart = new FirestoreRecyclerOptions.Builder<ViewCartPOJO>()
                .setQuery(query,ViewCartPOJO.class)
                .build();
        adapter = new ViewCartFirestoreAdapter(viewcart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
            db.collection(CUSTOMER).document(DOC_ID).collection(COST).document(COST)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    double cost = documentSnapshot.getDouble("cost");
                    if(cost == 0){
                        sendkotreq.setEnabled(false);
                    }
                    else{
                        sendkotreq.setEnabled(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    sendkotreq.setEnabled(false);
                }
            });

    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
