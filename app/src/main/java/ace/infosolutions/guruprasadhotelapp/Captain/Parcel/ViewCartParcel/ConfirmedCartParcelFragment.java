package ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelFragment;
import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelHistoryModel;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ConfirmedCartCaptainAdapter;
import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBillFirestoreAdapter;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.GrandTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.OnlineTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.ParcelTotalModel;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Captain.ItemList.CURRENT_KOT;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.PARCEL_ID_KEY;
import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.AddParcel.SP_KEY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.DAILY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.GRANDTOTAL;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.MONTHLY;
import static ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ConfirmFinalBill.TALLY;

public class ConfirmedCartParcelFragment extends Fragment {
    public static final String PARCEL_HISTORY = "PARCEL_HISTORY";
    private static final String PARCELS = "PARCELS";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    public static final String ONLINETOTAL = "ONLINETOTAL";
    private SharedPreferences sharedPreferences;
    private String DOC_ID = "";
    private ConfirmedCartCaptainAdapter adapter;
    private RecyclerView recyclerView, recyclerView2;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private CollectionReference confirmedRef, parcelRef,tallyRef;

    private ConfirmFinalBillFirestoreAdapter adapter2;
    private TextView total_cost;
    private Button print_finalbill, payment;
    private View paymentView;
    private TextView online_payment, cash_payment;
    private AlertDialog paymentAlert;
    private AlertDialog.Builder builder;
    private String Bill_NO;
    private String completed_date;


    private double final_confirmed_cost;
    private String date_time, payment_mode;
    private boolean ishomedelivery;
    private String cust_address, customer_contact, customer_name;

    private View editqtyView, editTitleView, editCostView;
    private EditText editqtyET, editCostET, editTitleET;
    private AlertDialog alertDialogTitle, alertDialogCost, alertDialogQty, alertDialogDelete;

    private View addYourself;
    private EditText addFoodTitle, addFoodCost, addFoodQty;
    private AlertDialog alertDialogAddyourself;

    private Button add_item;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmedcart_fragmentparcel, container, false);
        View view2 = inflater.inflate(R.layout.confirmedcart_fragmentparcel_manager, container, false);
        recyclerView = view.findViewById(R.id.recycler_confirmed);
        recyclerView2 = view2.findViewById(R.id.recycler_parcel_manager);
        print_finalbill = view2.findViewById(R.id.print);
        payment = view2.findViewById(R.id.payment);
        total_cost = view2.findViewById(R.id.total_cost);
        layoutManager = new LinearLayoutManager(getContext());
        builder = new AlertDialog.Builder(getContext());
        paymentAlert = builder.create();
        db = FirebaseFirestore.getInstance();
        tallyRef = db.collection(TALLY);
        sharedPreferences = getContext().getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        DOC_ID = sharedPreferences.getString(PARCEL_ID_KEY, "");
        confirmedRef = db.collection(PARCELS).document(DOC_ID).collection(CONFIRMED_KOT);
        parcelRef = db.collection(PARCELS);
        //payment alertdialog
        paymentView = inflater.inflate(R.layout.payment_alertdialog, null);
        online_payment = paymentView.findViewById(R.id.online_payment);
        cash_payment = paymentView.findViewById(R.id.cash_payment);


        if (ParcelFragment.ismanager) {
            add_item = view2.findViewById(R.id.add_item);
            editTitleView = inflater.inflate(R.layout.edittitlecurrentcart_alertdialog, null);
            editTitleET = editTitleView.findViewById(R.id.edit_title);
            alertDialogTitle = builder.create();

            editCostView = inflater.inflate(R.layout.editcostcurrentcart_alertdialog, null);
            editCostET = editCostView.findViewById(R.id.edit_cost);
            alertDialogCost = builder.create();

            editqtyView = inflater.inflate(R.layout.editqtycurrentcart_alertdialog, null);
            editqtyET = editqtyView.findViewById(R.id.edit_qty);
            alertDialogQty = builder.create();

            alertDialogDelete = builder.create();

            addYourself = inflater.inflate(R.layout.addyourselft_alertdialog, null);
            addFoodTitle = addYourself.findViewById(R.id.enter_foodtitle);
            addFoodCost = addYourself.findViewById(R.id.enter_foodcost);
            addFoodQty = addYourself.findViewById(R.id.enter_foodqty);
            addFoodCost.setHint("Enter total cost");
            addFoodQty.setHint("Enter quantity");
            addFoodTitle.setHint("Enter item name");
            alertDialogAddyourself = builder.create();

            return view2;
        } else {
            return view;
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ParcelFragment.ismanager) {
            setTotalCost();
            add_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUpItemAddAlert();
                }
            });
            print_finalbill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO PRINT BILL IMPLEMENTATION
                    final ProgressDialog progressDialog = ProgressDialog.show(getContext(),
                            "Print Bill", "Printing Final Bill", false);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Final Bill Printed", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);

                }
            });
            payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentAlert.setView(paymentView);
                    cash_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            paymentAlert.dismiss();
                            payment_mode = "Cash";
                            confirmFinalBill();
                        }
                    });

                    online_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            paymentAlert.dismiss();
                            payment_mode = "Online";
                            confirmFinalBill();
                        }
                    });
                    paymentAlert.show();
                }
            });
            setupRecyclerView2();
            adapter2.setOnFinalBillItemTitleClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemTitleClick() {
                @Override
                public void onItemClick(DocumentSnapshot snapshot, int pos) {
                    setUpItemTitleAlert(snapshot, pos);
                }
            });
            adapter2.setOnFinalBillItemCostClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemCostClick() {
                @Override
                public void onItemClick(DocumentSnapshot snapshot, int pos) {
                    setUpItemCostAlert(snapshot, pos);
                }
            });
            adapter2.setOnFinalBillItemQtyClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemQtyClick() {
                @Override
                public void onItemClick(DocumentSnapshot snapshot, int pos) {
                    setUpItemQtyAlert(snapshot, pos);
                }
            });
            adapter2.setOnFinalBillDeleteClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillDeleteClick() {
                @Override
                public void onItemClick(DocumentSnapshot snapshot, int pos) {
                    setUpItemDeleteAlert(snapshot, pos);
                }
            });
        } else {
            setupRecyclerView();
        }
    }

    private void setUpItemAddAlert() {
        alertDialogAddyourself.setTitle("Enter food details");
        alertDialogAddyourself.setIcon(R.drawable.nav_food_menu);
        alertDialogAddyourself.setView(addYourself);
        addFoodCost.setText("");
        addFoodTitle.setText("");
        addFoodQty.setText("");

        alertDialogAddyourself.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogAddyourself.dismiss();
            }
        });

        alertDialogAddyourself.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String getFoodQty = addFoodQty.getText().toString().trim();
                String getFoodTitle = addFoodTitle.getText().toString().trim();
                String getFoodCost = addFoodCost.getText().toString().trim();
                if (getFoodCost.equals("") || getFoodQty.equals("") || getFoodTitle.equals("")) {
                    alertDialogAddyourself.dismiss();
                    Toast.makeText(getContext(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                } else {
                    double food_cost = Double.parseDouble(getFoodCost);
                    int food_qty = Integer.parseInt(getFoodQty);
                    if (food_cost == 0.0 || food_qty == 0) {
                        alertDialogAddyourself.dismiss();
                        Toast.makeText(getContext(), "Cost/Qty cannot be zero!", Toast.LENGTH_SHORT).show();
                    } else {
                        FinalBillModel model = new FinalBillModel(getFoodTitle, food_cost, food_qty);
                        addToFinalBill(model);
                    }
                }
            }

            private void addToFinalBill(FinalBillModel model) {
                final double cost = model.getItem_cost();
                confirmedRef.add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            update_conf_cost(cost);
                        } else {
                            Toast.makeText(getContext(), "Failed to add item!", Toast.LENGTH_SHORT);
                        }
                    }

                    private void update_conf_cost(final double cost) {
                        parcelRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    double current_conf_cost = task.getResult().getDouble("confirmed_cost");
                                    double total_cost = current_conf_cost + cost;
                                    parcelRef.document(DOC_ID).update("confirmed_cost", total_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            setTotalCost();
                                            Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        alertDialogAddyourself.setCancelable(false);
        alertDialogAddyourself.show();
    }

    private void setUpItemDeleteAlert(final DocumentSnapshot snapshot, final int pos) {
        //delete the item
        String itemTitle = snapshot.getString("item_title");
        alertDialogDelete.setTitle("Delete item!");
        alertDialogDelete.setMessage("Are you sure want to delete " + itemTitle + "?");
        alertDialogDelete.setIcon(R.drawable.ic_delete);
        alertDialogDelete.setCancelable(false);
        alertDialogDelete.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialogDelete.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateConfirmed_cost(snapshot, pos);
            }

            private void updateConfirmed_cost(final DocumentSnapshot snapshot, final int pos) {
                final double cost_to_deduct = snapshot.getDouble("item_cost");
                parcelRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                            double final_cost = confirmed_cost - cost_to_deduct;
                            parcelRef.document(DOC_ID).update("confirmed_cost", final_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteCurrentItem(snapshot, pos);
                                    } else {
                                        //failed
                                    }
                                }
                            });
                        } else {
                            //failed
                        }
                    }
                });
            }

            private void deleteCurrentItem(DocumentSnapshot snapshot, final int pos) {
                String id = snapshot.getId();
                //TODO UPDATE THE CONFIRMED_COST FIRST
                confirmedRef.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                            adapter2.notifyItemRemoved(pos);
                            adapter2.notifyDataSetChanged();
                            setTotalCost();
                        } else {
                            Toast.makeText(getContext(), "Failed to delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialogDelete.show();
    }

    private void setUpItemQtyAlert(final DocumentSnapshot snapshot, int pos) {
        editqtyET.setText("");
        editqtyET.setHint(String.valueOf(snapshot.getDouble("item_qty").intValue()));
        alertDialogQty.setTitle("Edit Item Qty");
        alertDialogQty.setView(editqtyView);
        alertDialogQty.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int qty = Integer.parseInt(editqtyET.getText().toString());
                if (qty == 0) {
                    Toast.makeText(getContext(), "Quantity is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    updateItemQty(snapshot, qty);
                }
            }

            private void updateItemQty(DocumentSnapshot snapshot, int qty) {
                final String id = snapshot.getId();
                double current_cost = snapshot.getDouble("item_cost");
                int current_qty = snapshot.getDouble("item_qty").intValue();
                double single_item_cost = current_cost / current_qty;
                final double new_cost = single_item_cost * qty;

                final double final_cost = new_cost - current_cost;

                confirmedRef.document(id).update("item_qty", qty).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateCost();
                        } else {
                            Toast.makeText(getContext(), "Failed to update quantity!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateCost() {
                        confirmedRef.document(id).update("item_cost", new_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateConfirmedCost();
                                } else {
                                    Toast.makeText(getContext(), "Failed to update cost!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    private void updateConfirmedCost() {
                        parcelRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    double saved_cost = task.getResult().getDouble("confirmed_cost");
                                    double f_Cost = saved_cost + final_cost;
                                    parcelRef.document(DOC_ID).update("confirmed_cost", f_Cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                setTotalCost();
                                                Toast.makeText(getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        alertDialogQty.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogQty.dismiss();
            }
        });
        alertDialogQty.setCancelable(false);
        alertDialogQty.show();
    }

    private void setUpItemCostAlert(final DocumentSnapshot snapshot, int pos) {
        editCostET.setText("");
        editCostET.setHint(String.valueOf(snapshot.getDouble("item_cost") / snapshot.getDouble("item_qty").intValue()));
        alertDialogCost.setTitle("Edit Item Cost for 1 Qty");
        alertDialogCost.setView(editCostView);
        alertDialogCost.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String costString = editCostET.getText().toString().trim();
                if (costString.equals(null) || costString.equals("")) {
                    Toast.makeText(getContext(), "Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    double cost = Double.parseDouble(editCostET.getText().toString());
                    if (cost == 0.0 || cost == 0) {
                        Toast.makeText(getContext(), "Cost cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        updateItemCost(snapshot, cost);
                    }
                }
            }

            private void updateItemCost(DocumentSnapshot snapshot, double cost) {
                String id = snapshot.getId();
                int saved_qty = snapshot.getDouble("item_qty").intValue();
                double final_cost = cost * saved_qty;

                double saved_cost = snapshot.getDouble("item_cost");
                final double cost_difference = final_cost - saved_cost;

                confirmedRef.document(id).update("item_cost", final_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateConfCost(cost_difference);

                        } else {
                            Toast.makeText(getContext(), "Failed to update the cost!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateConfCost(final double cost_difference) {
                        parcelRef.document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                                    final double f_cost = confirmed_cost + cost_difference;
                                    parcelRef.document(DOC_ID).update("confirmed_cost", f_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                double roundedDouble = Math.round(f_cost * 100.0) / 100.0;
                                                total_cost.setText("Total Cost:Rs." + roundedDouble);
                                                Toast.makeText(getContext(), "Cost updated", Toast.LENGTH_SHORT).show();
                                            } else {

                                            }
                                        }
                                    });
                                } else {

                                }
                            }
                        });
                    }
                });
            }
        });
        alertDialogCost.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogCost.dismiss();
            }
        });
        alertDialogCost.setCancelable(false);
        alertDialogCost.show();
    }

    private void setUpItemTitleAlert(final DocumentSnapshot snapshot, int pos) {
        editTitleET.setText("");
        editTitleET.setHint(snapshot.getString("item_title"));
        alertDialogTitle.setTitle("Edit Item Title");
        alertDialogTitle.setView(editTitleView);
        alertDialogTitle.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String itemT = editTitleET.getText().toString().trim();
                if (itemT.equals(null) || itemT.equals("")) {
                    Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                } else {
                    updateItemTitle(snapshot, itemT);
                }
            }

            private void updateItemTitle(DocumentSnapshot snapshot, String itemT) {
                String id = snapshot.getId();
                confirmedRef.document(id).update("item_title", itemT).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Title updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update the title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialogTitle.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogTitle.dismiss();
            }
        });
        alertDialogTitle.setCancelable(false);
        alertDialogTitle.show();
    }

    private void setTotalCost() {
        db.collection(PARCELS).document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double total_Cost = task.getResult().getDouble("confirmed_cost");
                    double roundedDouble = Math.round(total_Cost * 100.0) / 100.0;
                    String final_cost = String.valueOf(roundedDouble);
                    total_cost.setText("Total:Rs." + final_cost);
                }
            }
        });
    }

    private void setupRecyclerView2() {
        Query query = confirmedRef;
        FirestoreRecyclerOptions<FinalBillModel> viewcart =
                new FirestoreRecyclerOptions.Builder<FinalBillModel>()
                        .setQuery(query, FinalBillModel.class)
                        .build();
        adapter2 = new ConfirmFinalBillFirestoreAdapter(viewcart, getView());
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setAdapter(adapter2);
    }

    private void setupRecyclerView() {
        Query query = confirmedRef;
        FirestoreRecyclerOptions<ViewCartModel> viewcart =
                new FirestoreRecyclerOptions.Builder<ViewCartModel>()
                        .setQuery(query, ViewCartModel.class)
                        .build();
        adapter = new ConfirmedCartCaptainAdapter(viewcart, getView());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ParcelFragment.ismanager) {
            adapter2.stopListening();
        } else {
            adapter.stopListening();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (ParcelFragment.ismanager) {
            adapter2.startListening();
        } else {
            adapter.startListening();
        }

    }

    private void generateCompletedDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        String datetoday = format.format(date);
        completed_date = datetoday.replaceAll("/", "-");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && ParcelFragment.isconfirmed == true && ParcelFragment.ismanager) {
            setTotalCost();
            ParcelFragment.isconfirmed = false;
        } else {
            ParcelFragment.isconfirmed = false;
        }
    }

    public void generateBillNo() {
        Random r = new Random();
        Date date = new Date();
        char a = (char) (r.nextInt(26) + 'a');
        char b = (char) (r.nextInt(26) + 'a');
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 5);
        Bill_NO = String.valueOf(a).concat(String.valueOf(b)).toUpperCase().concat(randomMilli);

    }

    private void confirmFinalBill() {
        db.collection(PARCELS).document(DOC_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final_confirmed_cost = task.getResult().getDouble("confirmed_cost");
                    date_time = task.getResult().getString("date_time");
                    ishomedelivery = task.getResult().getBoolean("ishomedelivery");
                    cust_address = task.getResult().getString("customer_address");
                    customer_name = task.getResult().getString("customer_name");
                    customer_contact = task.getResult().getString("customer_contact");

                    if (final_confirmed_cost == 0.0 || final_confirmed_cost == 0) {
                        Toast.makeText(getContext(), "There is nothing to confirm!", Toast.LENGTH_SHORT).show();
                    } else {
                        saveToHistory();
                    }
                }
            }

            private void saveToHistory() {
                generateBillNo();
                generateCompletedDateTime();
                ParcelHistoryModel model = new ParcelHistoryModel(Bill_NO, customer_name, customer_contact, ishomedelivery, cust_address,
                        final_confirmed_cost, date_time, completed_date, payment_mode);
                db.collection(PARCEL_HISTORY).add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            final String history_doc_id = task.getResult().getId();
                            confirmedRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            FinalBillModel model = snapshot.toObject(FinalBillModel.class);
                                            db.collection(PARCEL_HISTORY).document(history_doc_id).collection(CONFIRMED_KOT)
                                                    .add(model);
                                        }

                                        confirmedRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        confirmedRef.document(snapshot.getId()).delete();
                                                    }
                                                }
                                            }
                                        });
                                        db.collection(PARCELS).document(DOC_ID).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        db.collection(PARCELS).document(DOC_ID).collection(CURRENT_KOT)
                                                                .document(snapshot.getId()).delete();
                                                    }
                                                }
                                            }
                                        });

                                        db.collection(PARCELS).document(DOC_ID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    addToGrandTotal();
                                                    addToParcelTotal();
                                                    if(payment_mode.equals("Online")){
                                                        addToOnlineTotal();
                                                    }
                                                    Toast.makeText(getContext(), "Order completed", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getContext(), Manager.class));
                                                    getActivity().overridePendingTransition(0, 0);
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
        });
    }

    private void addToOnlineTotal() {
        final String date_only = date_time.substring(0,8);
        final String month_only = date_time.substring(3,8);

        tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        double stored_gt = task.getResult().getDouble("onlinetotal");
                        double total_gt = stored_gt + final_confirmed_cost;
                        OnlineTotalModel model = new OnlineTotalModel(total_gt,date_only);
                        tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only)
                                .set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addtoOnlineMonthly();
                            }
                        });

                    }
                    else{
                        OnlineTotalModel model = new OnlineTotalModel(final_confirmed_cost,date_only);
                        tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addtoOnlineMonthly();
                                }
                            }
                        });
                    }
                }
            }

            private void addtoOnlineMonthly() {
                tallyRef.document(MONTHLY).collection(ONLINETOTAL).document(month_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                //exists
                                double stored_gt = task.getResult().getDouble("onlinetotal");
                                double total_gt = final_confirmed_cost + stored_gt;
                                OnlineTotalModel model = new OnlineTotalModel(total_gt,month_only);
                                tallyRef.document(MONTHLY).collection(ONLINETOTAL).document(month_only).
                                        set(model);
                            }
                            else{
                                //doesn't exist
                                OnlineTotalModel model = new OnlineTotalModel(final_confirmed_cost,month_only);
                                tallyRef.document(MONTHLY).collection(ONLINETOTAL).document(month_only).set(model);
                            }
                        }
                    }
                });


            }
        });
    }

    private void addToParcelTotal() {
        final String date_only = date_time.substring(0,8);
        final String month_only = date_time.substring(3,8);

        tallyRef.document(DAILY).collection(PARCELS).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        double stored_gt = task.getResult().getDouble("parceltotal");
                        double total_gt = stored_gt + final_confirmed_cost;
                        ParcelTotalModel model = new ParcelTotalModel(total_gt,date_only);
                        tallyRef.document(DAILY).collection(PARCELS).document(date_only)
                                .set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addtoParcelMonthly();
                            }
                        });

                    }
                    else{
                        ParcelTotalModel model = new ParcelTotalModel(final_confirmed_cost,date_only);
                        tallyRef.document(DAILY).collection(PARCELS).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addtoParcelMonthly();
                                }
                            }
                        });
                    }
                }
            }

            private void addtoParcelMonthly() {
                tallyRef.document(MONTHLY).collection(PARCELS).document(month_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                //exists
                                double stored_gt = task.getResult().getDouble("parceltotal");
                                double total_gt = final_confirmed_cost + stored_gt;
                                ParcelTotalModel model = new ParcelTotalModel(total_gt,month_only);
                                tallyRef.document(MONTHLY).collection(PARCELS).document(month_only).
                                        set(model);
                            }
                            else{
                                //doesn't exist
                                ParcelTotalModel model = new ParcelTotalModel(final_confirmed_cost,month_only);
                                tallyRef.document(MONTHLY).collection(PARCELS).document(month_only).set(model);
                            }
                        }
                    }
                });


            }
        });

    }

    private void addToGrandTotal() {
        final String date_only = date_time.substring(0,8);
        final String month_only = date_time.substring(3,8);


        tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        double stored_gt = task.getResult().getDouble("grandtotal");
                        double total_gt = stored_gt + final_confirmed_cost;
                        GrandTotalModel model = new GrandTotalModel(total_gt,date_only);
                        tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only)
                                .set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addToGTMonthly();
                            }
                        });

                    }
                    else{
                        GrandTotalModel model = new GrandTotalModel(final_confirmed_cost,date_only);
                        tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addToGTMonthly();
                                }
                            }
                        });
                    }
                }
            }

            private void addToGTMonthly() {
                tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                //exists
                                double stored_gt = task.getResult().getDouble("grandtotal");
                                double total_gt = final_confirmed_cost + stored_gt;
                                GrandTotalModel model = new GrandTotalModel(total_gt,month_only);
                                tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only).
                                        set(model);
                            }
                            else{
                                //doesn't exist
                                GrandTotalModel model = new GrandTotalModel(final_confirmed_cost,month_only);
                                tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only).set(model);
                            }
                        }
                    }
                });


            }
        });
    }
}