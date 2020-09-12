package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import ace.infosolutions.guruprasadhotelapp.R;

public class CurrentCartFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewCartFirestoreAdapter adapter;
    private CollectionReference collectionReference = db.collection("Customers");
    private TextView total_cost_order;
    double sum = 0;

    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private SharedPreferences sharedPreferences;

    private String DOC_ID = "";
    private final String CUSTOMER = "Customers";
    private final String KOT = "KOT";
    private final String FINAL_BILL = "FINAL_BILL";
    private final String COST = "COST";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentcart_fragment,container,false);
        recyclerView = view.findViewById(R.id.currentcart_recycler);
        layoutManager = new LinearLayoutManager(view.getContext());
        total_cost_order = view.findViewById(R.id.total_cost_order);
        sharedPreferences= getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY,"");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerview();
        setTotalCost();

        adapter.setOnItemCartClickListener(new ViewCartFirestoreAdapter.OnItemClickListenerCart() {
            @Override
            public void onItemClickCart(DocumentSnapshot documentSnapshot, final int position) {

                //TODO Update the FINAL_BILL Collection

                String id = documentSnapshot.getId();
                ViewCartPOJO cartPOJO = documentSnapshot.toObject(ViewCartPOJO.class);
                final String foodTitle = cartPOJO.getFood_title();
                final int foodQty = cartPOJO.getFood_qty();
                final double cost = documentSnapshot.getDouble("food_cost");
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
        });
    }

    private void updateFinalBill(String foodTitle, int foodQty, final int position) {
        collectionReference.document(DOC_ID)
                .collection(FINAL_BILL).whereEqualTo("item_title",foodTitle).whereEqualTo("item_qty",foodQty)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //TODO UNCOMPLETE CODE
                QuerySnapshot querySnapshot = task.getResult();
                for(QueryDocumentSnapshot snapshot: querySnapshot){
                    String id = snapshot.getId();
                    collectionReference.document(DOC_ID).collection(FINAL_BILL)
                            .document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
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
                .document(COST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = 0;
                    try {
                        cost = snapshot.getDouble("cost");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        total_cost_order.setText("Cart is empty");
                    }

                    if(cost<0){
                        cost = 0;
                        total_cost_order.setText("Total Cost: Rs."+cost);
                    }
                    else{
                        total_cost_order.setText("Total Cost: Rs."+cost);
                    }

                }

            }
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
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
