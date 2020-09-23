package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.FoodMenu;
import ace.infosolutions.guruprasadhotelapp.R;


public class CurrentCartFragment extends Fragment {
    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private ViewCartFirestoreAdapter adapter;
    private Button reqKotButton;
    private CollectionReference customerRef;
    private TextView total_Cost_Order;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentcart_fragment,container,false) ;
        sharedPreferences = getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY, "");
        recyclerView= view.findViewById(R.id.currentcart_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        db = FirebaseFirestore.getInstance();
        reqKotButton= view.findViewById(R.id.sendkotreq);
        customerRef = db.collection(CUSTOMERS);
        total_Cost_Order = view.findViewById(R.id.total_cost_order);
        builder = new AlertDialog.Builder(getContext());
        alertDialog=builder.create();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTotalCurrentCost();
        setUpRecyclerView();
        reqKotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            double current_cost = task.getResult().getDouble("current_cost");
                            if(current_cost == 0){
                                reqKotButton.setEnabled(false);
                                Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                reqKotButton.setEnabled(false);
                                customerRef.document(DOC_ID).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                                FoodItemModel model = snapshot.toObject(FoodItemModel.class);
                                                customerRef.document(DOC_ID).collection("REQUESTED_KOT").add(model).
                                                        addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful()){
                                                                    resetCurrentCost();
                                                                    // Toast.makeText(getContext(), "KOT Requested!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else{
                                                                    Toast.makeText(getContext(), "Failed to request KOT", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                        else{
                                            Toast.makeText(getContext(), "Cannot request KOT", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });
                //Copying current_kot documents to Requested_kot collection

            }
        });

        adapter.setOnItemCartClickListener(new ViewCartFirestoreAdapter.OnItemClickListenerCart() {
            @Override
            public void onItemClickCart(final DocumentSnapshot documentSnapshot, final int position) {
                alertDialog.setTitle("Are you sure want to delete the item?");
                alertDialog.setIcon(R.drawable.ic_delete);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCurrentItem(documentSnapshot,position);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    private void deleteCurrentItem(final DocumentSnapshot documentSnapshot, int position) {
        String current_kotdocid = documentSnapshot.getId();
        customerRef.document(DOC_ID).collection(CURRENT_KOT).document(current_kotdocid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //getting current cost to deduct the delete cost
                    customerRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                double current_cost = task.getResult().getDouble("current_cost");
                                FoodItemModel model = documentSnapshot.toObject(FoodItemModel.class);
                                double deleted_item_cost = model.getItem_cost();
                                double final_cost = current_cost - deleted_item_cost;
                                Map<String,Object> updated_currentcostMap = new HashMap<>();
                                updated_currentcostMap.put("current_cost",final_cost);
                                customerRef.document(DOC_ID).update(updated_currentcostMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            setTotalCurrentCost();
                                            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
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

    private void setTotalCurrentCost() {
        customerRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String total_cost = String.valueOf(task.getResult().getDouble("current_cost"));
                    total_Cost_Order.setText("Total Cost: Rs."+total_cost);
                }
            }
        });
    }

    private void resetCurrentCost() {
        //Update the current_cost field to zero and copy the value to Requested_KOT
        final Map<String,Object> req_cost = new HashMap<>();
        customerRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    double initialRequested_cost = task.getResult().getDouble("requested_cost");
                    double requested_cost = task.getResult().getDouble("current_cost");
                    double final_requ_cost = requested_cost+initialRequested_cost;
                    req_cost.put("requested_cost",final_requ_cost);
                    customerRef.document(DOC_ID).update(req_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                deleteCurrentKotColl();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteCurrentKotColl() {
        //clearing the entire Current_KOT Collection
        customerRef.document(DOC_ID).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        customerRef.document(DOC_ID).collection(CURRENT_KOT).document(snapshot.getId()).delete();
                    }
                }
                updateKOTRequested();
            }
        });
    }

    private void updateKOTRequested() {
        //Update the boolean value kotrequested to true to let know the manager kot is requested
        Map<String,Object> kotRequested = new HashMap<>();
        kotRequested.put("kotrequested",true);
        customerRef.document(DOC_ID).update(kotRequested).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setCurrentCosttoZero();
                }
            }
        });
    }

    private void setCurrentCosttoZero() {
        Map<String,Object> setcurrentcost_zero = new HashMap<>();
        setcurrentcost_zero.put("current_cost",0);
        customerRef.document(DOC_ID).update(setcurrentcost_zero).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setTotalCurrentCost();
                    Toast.makeText(getContext(), "KOT Requested!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = customerRef.document(DOC_ID).collection(CURRENT_KOT);
        FirestoreRecyclerOptions<ViewCartPOJO> viewcart = new FirestoreRecyclerOptions.Builder<ViewCartPOJO>()
                .setQuery(query, ViewCartPOJO.class)
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



    /*    public static final String PREF_DOCID = "PREF_DOCID";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private final String CUSTOMER = "Customers";
    private final String KOT = "KOT";
    private final String FINAL_BILL = "FINAL_BILL";
    private final String COST = "COST";
    double sum = 0;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewCartFirestoreAdapter adapter;
    private CollectionReference collectionReference = db.collection("Customers");
    private TextView total_cost_order;
    private Button sendkotreq;
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Map<String, Object> isrequested_map;
    private Map<String, Object> update_parentKOT;

    private View editQtyView;
    private AlertDialog.Builder editQtybuilder;
    private EditText editQtyET;
    private AlertDialog editQtyalert;

    private ProgressBar progressbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentcart_fragment, container, false);
        recyclerView = view.findViewById(R.id.currentcart_recycler);
        layoutManager = new LinearLayoutManager(view.getContext());

        editQtyView = inflater.inflate(R.layout.editqtycurrentcart_alertdialog,null);
        editQtyET = (EditText)editQtyView.findViewById(R.id.edit_qty);
        editQtybuilder = new AlertDialog.Builder(getContext());


        sendkotreq = (Button) view.findViewById(R.id.sendkotreq);
        total_cost_order = view.findViewById(R.id.total_cost_order);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar_currentcart);
        isrequested_map = new HashMap<>();
        isrequested_map.put("isrequested", true);
        update_parentKOT = new HashMap<>();
        update_parentKOT.put("kotrequested", true);
        builder = new AlertDialog.Builder(getContext());
        sharedPreferences = getContext().getSharedPreferences(PREF_DOCID, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(DOC_ID_KEY, "");
        editQtyalert = editQtybuilder.create();
        editQtyalert.setCancelable(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setUpQtyEditAlertdialog();
        setupRecyclerview();
        // setTotalCost();

        sendkotreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkifCartEmpty();
            }
        });

    *//*    adapter.setOnQtyClickListener(new ViewCartFirestoreAdapter.onQtyClickListener() {
            @Override
            public void onQtyClick(DocumentSnapshot snapshot) {
               //TODO EDIT QTY OF SELECTED FOOD ITEM IN CURRENT CART LIST
                editQtyET.setText("");
                final String item_title = snapshot.getString("item_title");
                final String doc_id_kot = snapshot.getId();
                final int item_qty = snapshot.getLong("item_qty").intValue();
                final double item_cost = snapshot.getDouble("item_cost");
                editQtyalert.setTitle(item_title);
                editQtyalert.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                               updateQty(doc_id_kot,item_qty,item_cost,item_title);
                    }
                });
                editQtyalert.show();
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
                                sendkotreq.setEnabled(false);
                                progressbar.setVisibility(View.VISIBLE);
                                collectionReference.document(DOC_ID)
                                        .collection(KOT).document(id)
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            updateCost(cost, position, foodTitle, foodQty);
                                        else
                                            Toast.makeText(getContext(), "Cannot delete item", Toast.LENGTH_SHORT).show();
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
    }*//*

  *//*  private void updateQty(String doc_id_kot,int item_qty,double item_cost,String item_title) {
        //TODO 1.Update the cost
        //TODO 2.Update KOT subcollection document field value(item_qty) & (item_cost)
        //TODO 3.Update Final subcollection document field value(item_qty) & (item_cost)
        if(editQtyET.getText().toString().equals("") || editQtyET.getText().toString().equals(null)){
            Toast.makeText(getContext(), "No value entered", Toast.LENGTH_SHORT).show();
        }
        else{
            int entered_qty = Integer.parseInt(editQtyET.getText().toString());
            if(entered_qty == 0)
                Toast.makeText(getContext(), "Should be atleast one", Toast.LENGTH_SHORT).show();
            else{
                updateQtyMain(doc_id_kot,item_qty,item_cost,entered_qty,item_title);
            }
        }

    }
*//*
 *//*   private void updateQtyMain(final String doc_id_kot, final int item_qty, final double current_item_cost, final int entered_qty, final String item_title) {
        double single_item_cost = current_item_cost/item_qty;
        final double final_item_cost = single_item_cost*entered_qty;
        final double cost_difference;

        if(current_item_cost== final_item_cost)
            cost_difference = 0;
        else {
            sendkotreq.setEnabled(false);
            progressbar.setVisibility(View.VISIBLE);
            cost_difference = current_item_cost - final_item_cost;
            db.collection(CUSTOMER).document(DOC_ID).collection(COST).document(COST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        double total_cost = task.getResult().getDouble("cost");
                        double changed_total_cost = total_cost - cost_difference;
                        Map<String,Object> update_total_cost = new HashMap<>();
                        update_total_cost.put("cost",changed_total_cost);
                        //Updating Total cost in COST subcollection
                        db.collection(CUSTOMER).document(DOC_ID).collection(COST).
                                document(COST).update(update_total_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Updating KOT item_qty value with new entered qty
                                    db.collection(CUSTOMER).document(DOC_ID).collection(KOT)
                                            .document(doc_id_kot).update("item_qty",entered_qty).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //updating KOT item_cost with new cost
                                            if(task.isSuccessful()) {
                                                db.collection(CUSTOMER).document(DOC_ID).collection(KOT)
                                                        .document(doc_id_kot).update("item_cost", final_item_cost)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    //updating Final Bill item_qty
                                                                    //TODO REM
                                                                    progressbar.setVisibility(View.GONE);
                                                                    sendkotreq.setEnabled(true);
                                                                    setTotalCost();
                                                                    Toast.makeText(getContext(), "Quantity updated!", Toast.LENGTH_SHORT).show();
                                                                   // updateFinalBillQty(item_qty,current_item_cost,entered_qty,item_title,final_item_cost);
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
                }
            });
        }
        Log.e("Cost Difference", String.valueOf(cost_difference));

    }*//*

   *//* private void updateFinalBillQty(int item_qty, double current_item_cost, final int entered_qty, final String item_title, final double final_item_cost) {
        db.collection(CUSTOMER).document(DOC_ID)
                .collection(FINAL_BILL).whereEqualTo("item_title", item_title).whereEqualTo("item_qty", item_qty).
                whereEqualTo("isconfirmed", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String final_bill_doc_id;
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                            final_bill_doc_id = snapshot.getId();
                        final String final_bill_doc_id1 = final_bill_doc_id;
                        db.collection(CUSTOMER).document(DOC_ID).collection(FINAL_BILL).document(final_bill_doc_id)
                                .update("item_qty",entered_qty).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    db.collection(CUSTOMER).document(DOC_ID).collection(FINAL_BILL)
                                            .document(final_bill_doc_id1)
                                            .update("item_cost",final_item_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressbar.setVisibility(View.GONE);
                                                sendkotreq.setEnabled(true);
                                                setTotalCost();
                                                Toast.makeText(getContext(), "Quantity updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        }
                }
            }
        });
    }*//*

      *//*  private void setUpQtyEditAlertdialog () {
            editQtyalert.setIcon(R.drawable.rupee);
            editQtyalert.setView(editQtyView);
            editQtyalert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editQtyalert.dismiss();
                }
            });

        }*//*


*//*    private void requestKOT() {
        db.collection(CUSTOMER).document(DOC_ID).collection(KOT).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                String KOT_doc_id = snapshot.getId();
                                db.collection(CUSTOMER).document(DOC_ID).collection(KOT).document(KOT_doc_id)
                                        .update(isrequested_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

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
                if (task.isSuccessful()) {

                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot snapshot : querySnapshot) {
                        String id = snapshot.getId();
                        collectionReference.document(DOC_ID).collection(FINAL_BILL)
                                .document(id).update(isrequested_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isCanceled())
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
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "KOT Request sent successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finishAffinity();
                    startActivity(new Intent(getContext(), FoodMenu.class));
                    getActivity().overridePendingTransition(0,0);
                }

                else
                    Toast.makeText(getContext(), "Unable to generate KOT", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkifCartEmpty() {
        db.collection(CUSTOMER).document(DOC_ID).collection(COST).document(COST).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            int cost = task.getResult().getLong("cost").intValue();
                            if (cost == 0)
                                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                            else
                                requestKOT();
                        }
                    }
                }
        );
    }


    private void updateFinalBill(String foodTitle, int foodQty, final int position) {
        collectionReference.document(DOC_ID)
                .collection(FINAL_BILL).whereEqualTo("item_title", foodTitle).whereEqualTo("item_qty", foodQty).
                whereEqualTo("isconfirmed", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot snapshot : querySnapshot) {
                        String id = snapshot.getId();
                        collectionReference.document(DOC_ID).collection(FINAL_BILL)
                                .document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyDataSetChanged();
                                    sendkotreq.setEnabled(true);
                                    progressbar.setVisibility(View.GONE);
                                } else
                                    Toast.makeText(getContext(), "Cannot delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
                if (cost == 0) {
                    total_cost_order.setText("Cart is empty");
                    setParentKOTRequestedfalse();
                } else {
                    total_cost_order.setText("Total Cost: Rs." + String.valueOf(cost));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Failed to retrieve cost", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setParentKOTRequestedfalse() {
        db.collection(CUSTOMER).document(DOC_ID).update("kotrequested", false);
    }

    private void updateCost(final double item_cost, final int position, final String foodTitle, final int foodQty) {
        db.collection(CUSTOMER).document(DOC_ID)
                .collection(COST)
                .document(COST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    double cost = snapshot.getDouble("cost");
                    cost = cost - item_cost;
                    Map<String, Object> map = new HashMap<>();
                    map.put("cost", cost);
                    setTotalCost();
                    db.collection(CUSTOMER).document(DOC_ID)
                            .collection(COST)
                            .document(COST).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                updateFinalBill(foodTitle, foodQty, position);
                            else
                                Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }*//*
    }
    private void setupRecyclerview() {
        Query query = collectionReference.document(DOC_ID).collection(CURRENT_KOT);
        FirestoreRecyclerOptions<ViewCartPOJO> viewcart = new FirestoreRecyclerOptions.Builder<ViewCartPOJO>()
                .setQuery(query, ViewCartPOJO.class)
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
    }*/
}
