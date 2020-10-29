package ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.Adapters.FoodItemModel;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelFragment;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.GenerateNumber;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;

public class CurrentCartParcelFragment extends Fragment {
    private static final String PARCELS = "PARCELS";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private ViewCartFirestoreAdapter adapter;
    private Button printKOT;
    private CollectionReference parcelRef, currentRef;
    private AlertDialog alertDialog, alertdialogEditQty;
    private AlertDialog.Builder builder;
    private View editQtyView;
    private EditText editQtyET;
    private Button printOnly;
    private ProgressBar progressBar;

    private GenerateNumber number = new GenerateNumber();
    private String kot_no;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentcart_fragment, container, false);
        sharedPreferences = getContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(PARCEL_ID_KEY, "");
        recyclerView = view.findViewById(R.id.currentcart_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        kot_no = number.generateBillNo();
        db = FirebaseFirestore.getInstance();
        printOnly = view.findViewById(R.id.pribtonly);
        progressBar = view.findViewById(R.id.progressbar);
        printKOT = view.findViewById(R.id.printkot);
        parcelRef = db.collection(PARCELS);
        currentRef = db.collection(PARCELS).document(DOC_ID).collection(CURRENT_KOT);
        builder = new AlertDialog.Builder(getContext());
        alertDialog = builder.create();
        editQtyView = inflater.inflate(R.layout.editqtycurrentcart_alertdialog, null);
        editQtyET = editQtyView.findViewById(R.id.edit_qty);
        alertdialogEditQty = builder.create();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();

        printOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatabaseValues();
            }


            private void getDatabaseValues() {

                final ArrayList<ViewCartModel> arrayList = new ArrayList<>();
                final StringBuffer buffer = new StringBuffer();

                parcelRef.document(DOC_ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        //final String cust_name = snapshot.getString("customer_name");
                        // final String cust_address = snapshot.getString("customer_address");
                        Double conf_cost = snapshot.getDouble("confirmed_cost");
                        // final String total_cost = String.valueOf(conf_cost);
                        // final String cust_contact = snapshot.getString("customer_contact");
                        boolean ishomedelivery = snapshot.getBoolean("ishomedelivery");
                        final String order_type;
                        if (ishomedelivery) {
                            order_type = "Home Delivery";
                        } else {
                            order_type = "Parcel";
                        }

                        if (conf_cost == 0.0) {
                            Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            currentRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                            double item_Cost = snapshot1.getDouble("item_cost");
                                            String item_Title = snapshot1.getString("item_title");
                                            int item_Qty = snapshot1.getDouble("item_qty").intValue();

                                            ViewCartModel getModel = new ViewCartModel(item_Title, item_Cost, item_Qty);
                                            if (getModel != null) {
                                                arrayList.add(getModel);
                                            }
                                        }
                                        if (!arrayList.isEmpty()) {
                                            for (int i = 0; i < arrayList.size(); i++) {
                                                buffer.append("\"[L]" + arrayList.get(i).getItem_title() + "[R]" + arrayList.get(i).getItem_qty() + "\\n\"" + "\n");
                                            }
                                            //bluetoothPrint(buffer, order_type);
                                        }
                                    }
                                }

                            });
                        }
                    }
                });
            }
        });

        printKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parcelRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            double current_cost = task.getResult().getDouble("current_cost");
                            if (current_cost == 0) {
                                printKOT.setEnabled(false);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
                            } else {
                                printKOT.setEnabled(false);
                                InternetConn conn = new InternetConn(getContext());
                                if (conn.haveNetworkConnection()) {
                                    final WriteBatch batch = db.batch();
                                    parcelRef.document(DOC_ID).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                ParcelFragment.isconfirmed = true;
                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                    FoodItemModel model = snapshot.toObject(FoodItemModel.class);
                                                    DocumentReference reference = parcelRef.document(DOC_ID).collection(CURRENT_KOT).document(snapshot.getId());
                                                    DocumentReference reference1 = parcelRef.document(DOC_ID).collection(CONFIRMED_KOT).document();
                                                    batch.set(reference1, model);
                                                    batch.delete(reference);
                                                }
                                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        db.runTransaction(new Transaction.Function<Void>() {
                                                            @Nullable
                                                            @Override
                                                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                                final DocumentReference parcelCostRef = parcelRef.document(DOC_ID);
                                                                DocumentSnapshot snapshot1 = transaction.get(parcelCostRef);
                                                                double current_cost = snapshot1.getDouble("current_cost");
                                                                double confirmed_cost = snapshot1.getDouble("confirmed_cost");
                                                                double final_cost = current_cost + confirmed_cost;
                                                                transaction.update(parcelCostRef, "confirmed_cost", final_cost);
                                                                transaction.update(parcelCostRef, "total_cost", final_cost);
                                                                transaction.update(parcelCostRef, "current_cost", 0);
                                                                return null;
                                                            }
                                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Cannot request KOT", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                });

            }

        });

        adapter.setOnQtyClickListener(new ViewCartFirestoreAdapter.onQtyClickListener() {
            @Override
            public void onQtyClick(final DocumentSnapshot snapshot) {
                //edit quantity
                FoodItemModel model = snapshot.toObject(FoodItemModel.class);
                String item_name = model.getItem_title();
                String item_cost = String.valueOf(model.getItem_qty());
                editQtyET.setText("");
                editQtyET.setHint(item_cost);
                alertdialogEditQty.setTitle("Edit Quantity of " + item_name);
                alertdialogEditQty.setView(editQtyView);
                alertdialogEditQty.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertdialogEditQty.dismiss();
                    }
                });
                alertdialogEditQty.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String entered_qty = editQtyET.getText().toString().trim();
                        if (entered_qty.equals("")) {
                            Toast.makeText(getContext(), "No value entered", Toast.LENGTH_SHORT).show();
                        } else {
                            int qty = Integer.parseInt(entered_qty);
                            if (qty == 0) {
                                Toast.makeText(getContext(), "No value entered!", Toast.LENGTH_SHORT).show();
                            } else {
                                updateItemQty(snapshot, qty);
                            }
                        }
                    }
                });
                alertdialogEditQty.show();
            }

            private void updateItemQty(DocumentSnapshot snapshot, final int qty) {
                progressBar.setVisibility(View.VISIBLE);
                final String id = snapshot.getId();
                double current_cost = snapshot.getDouble("item_cost");
                int current_qty = snapshot.getDouble("item_qty").intValue();
                double single_item_cost = current_cost / current_qty;
                final double new_cost = single_item_cost * qty;
                final double final_cost = new_cost - current_cost;

                final DocumentReference parentRef = parcelRef.document(DOC_ID);
                final DocumentReference currentKOTRef = currentRef.document(id);


                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        double saved_cost = transaction.get(parentRef).getDouble("current_cost");
                        double f_Cost = saved_cost + final_cost;
                        transaction.update(parentRef, "current_cost", f_Cost);
                        transaction.update(currentKOTRef, "item_qty", qty);
                        transaction.update(currentKOTRef, "item_cost", new_cost);
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                });
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
                        deleteCurrentItem(documentSnapshot, position);
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

            private void deleteCurrentItem(final DocumentSnapshot documentSnapshot, int position) {
                progressBar.setVisibility(View.VISIBLE);
                String current_kotdocid = documentSnapshot.getId();
                final DocumentReference currentKOTRef = parcelRef.document(DOC_ID).collection(CURRENT_KOT).document(current_kotdocid);
                final DocumentReference parentRef = parcelRef.document(DOC_ID);
                //
                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(parentRef);
                        double current_cost = snapshot.getDouble("current_cost");
                        FoodItemModel model = documentSnapshot.toObject(FoodItemModel.class);
                        double deleted_item_cost = model.getItem_cost();
                        double final_cost = current_cost - deleted_item_cost;
                        Map<String, Object> updated_currentcostMap = new HashMap<>();
                        updated_currentcostMap.put("current_cost", final_cost);
                        transaction.update(parentRef, updated_currentcostMap);
                        transaction.delete(currentKOTRef);
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = parcelRef.document(DOC_ID).collection(CURRENT_KOT);
        FirestoreRecyclerOptions<ViewCartModel> viewcart = new FirestoreRecyclerOptions.Builder<ViewCartModel>()
                .setQuery(query, ViewCartModel.class)
                .build();
        adapter = new ViewCartFirestoreAdapter(viewcart, getView());
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
