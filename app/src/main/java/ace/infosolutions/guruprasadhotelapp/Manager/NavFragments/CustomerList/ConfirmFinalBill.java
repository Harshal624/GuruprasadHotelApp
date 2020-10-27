package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.GrandTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.HistoryModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.OnlineTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.TableTotalModel;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.GenerateNumber;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

import static ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ViewCartParcel.ConfirmedCartParcelFragment.ONLINETOTAL;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PRINTERNBRCHARACTERSPERLINE;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PRINTER_DPI;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PRINTER_WIDTHmm;

public class ConfirmFinalBill extends AppCompatActivity {
    //Strings for Tally
    public static final String TALLY = "TALLY";
    public static final String DAILY = "Daily";
    public static final String MONTHLY = "Monthly";
    public static final String HISTORY = "HISTORY";
    public static final String TABLETALLYDAILY = "TABLETALLYDAILY";
    public static final String GRANDTOTAL = "GRANDTOTAL";
    public static final String TOTALONLINEBILL = "TOTALONLINEBILL";
    public static final String PARCEHOMEDELIVERY = "PARCEHOMEDELIVERY";
    public static final String TABLETALLYMONTHLY = "TABLETALLYMONTHLY";
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String TABLES = "Tables";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    GenerateNumber number = new GenerateNumber();
    private String completed_date;
    //
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ConfirmFinalBillFirestoreAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference confirmedRef, custRef, historyRef, tallyRef;
    private String doc_id;
    private TextView tableNo, tableType, totalCost;
    private Button printBill, addItem;
    private AlertDialog alertDialogQty, alertDialogTitle, alertDialogCost, alertDialogDelete, alertDialogAddyourself;
    private AlertDialog.Builder builder;
    private View editqtyView, editTitleView, editCostView;
    private EditText editqtyET, editCostET, editTitleET;
    private View addYourself;
    private EditText addFoodTitle, addFoodCost, addFoodQty;
    private String table_type, date_time;
    private int table_no, no_of_cust;
    private ProgressBar progressBar;
    private double final_confirmed_cost;
    private Button payment;
    private AlertDialog paymentAlertDialog;
    private View paymentView;
    private TextView online_payment, cash_payment;
    private String Bill_NO;
    private double current_costFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_bill);
        Bill_NO = number.generateBillNo();
        completed_date = number.generateCompletedDateTime();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.final_bill_recycler);
        doc_id = getIntent().getStringExtra("FinalDOCID");
        addItem = findViewById(R.id.add_item);
        payment = findViewById(R.id.payment);
        layoutManager = new LinearLayoutManager(this);
        confirmedRef = db.collection(CUSTOMERS).document(doc_id).collection(CONFIRMED_KOT);
        custRef = db.collection(CUSTOMERS);
        builder = new AlertDialog.Builder(this);
        alertDialogQty = builder.create();
        alertDialogTitle = builder.create();
        alertDialogCost = builder.create();
        alertDialogDelete = builder.create();
        alertDialogAddyourself = builder.create();
        paymentAlertDialog = builder.create();
        progressBar = findViewById(R.id.progressBar);
        tallyRef = db.collection(TALLY);

        historyRef = db.collection(HISTORY);

        editqtyView = LayoutInflater.from(this).inflate(R.layout.editqtycurrentcart_alertdialog, null);
        editqtyET = editqtyView.findViewById(R.id.edit_qty);

        paymentView = LayoutInflater.from(this).inflate(R.layout.payment_alertdialog, null);
        online_payment = paymentView.findViewById(R.id.online_payment);
        cash_payment = paymentView.findViewById(R.id.cash_payment);


        editTitleView = LayoutInflater.from(this).inflate(R.layout.edittitlecurrentcart_alertdialog, null);
        editTitleET = editTitleView.findViewById(R.id.edit_title);

        editCostView = LayoutInflater.from(this).inflate(R.layout.editcostcurrentcart_alertdialog, null);
        editCostET = editCostView.findViewById(R.id.edit_cost);

        addYourself = LayoutInflater.from(this).inflate(R.layout.addyourselft_alertdialog, null);
        addFoodTitle = addYourself.findViewById(R.id.enter_foodtitle);
        addFoodCost = addYourself.findViewById(R.id.enter_foodcost);
        addFoodQty = addYourself.findViewById(R.id.enter_foodqty);
        addFoodCost.setHint("Enter total cost");
        addFoodQty.setHint("Enter quantity");
        addFoodTitle.setHint("Enter item name");


        printBill = findViewById(R.id.final_billPrint);
        tableNo = findViewById(R.id.final_billtableno);
        tableType = findViewById(R.id.final_billtabletype);
        totalCost = findViewById(R.id.final_billTotalCost);

        fetchTableInfoTotalcost();
        setupRecyclerView();
        printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printBill.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                printFinalBill();
            }

            private void printFinalBill() {
                //print final bill
                final ArrayList<ViewCartModel> arrayList = new ArrayList<>();
                final StringBuffer buffer = new StringBuffer();

                custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        final String table_no = String.valueOf(snapshot.getDouble("table_no").intValue());
                        final String table_type = snapshot.getString("table_type");
                        double conf_cost = snapshot.getDouble("confirmed_cost");
                        if (conf_cost == 0.0) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ConfirmFinalBill.this, "Cart is empty", Toast.LENGTH_SHORT).show();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            confirmedRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            double item_Cost = snapshot.getDouble("item_cost");
                                            String item_Title = snapshot.getString("item_title");
                                            int item_Qty = snapshot.getDouble("item_qty").intValue();
                                            ViewCartModel getModel = new ViewCartModel(item_Title, item_Cost, item_Qty);
                                            if (getModel != null) {
                                                arrayList.add(getModel);
                                            }
                                        }
                                        if (!arrayList.isEmpty() && Bill_NO != null && table_no != null &&
                                                table_type != null && date_time != null) {
                                            for (int i = 0; i < arrayList.size(); i++) {
                                                buffer.append("\"[L]" + arrayList.get(i).getItem_title() + "\"[C]" + arrayList.get(i).getItem_qty() + "[R]" + arrayList.get(i).getItem_cost() + "\\n\"" + "\n");
                                            }
                                            bluetoothPrint(buffer, Bill_NO, table_no, table_type, date_time);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });


            }

            private void bluetoothPrint(final StringBuffer buffer, final String BILL_N, final String table_no, final String table_type, final String date) {

                if (ContextCompat.checkSelfPermission(ConfirmFinalBill.this, Manifest.permission.BLUETOOTH) ==
                        PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EscPosPrinter posPrinter = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(),
                                        PRINTER_DPI, PRINTER_WIDTHmm, PRINTERNBRCHARACTERSPERLINE);
                                posPrinter.printFormattedText(
                                        "[C]<u><font size='big'>Hotel Guruprasad</font></u>" +
                                                "[L]\n" +
                                                "[C]<font size='small'>Mahadevnagar, Islampur</font>" +
                                                "[L]\n" +
                                                "[C]================================\n" +
                                                "[L]\n" +
                                                "[C]<b>BILL NO:" + "[R]" + BILL_N + "</b>" + "\n" +
                                                "[L]Date:" + "[R]" + date + "\n" +
                                                "[L]Table No:" + "[R]" + table_no + "\n" +
                                                "[L]Table Type:" + "[R]" + table_type + "\n" +
                                                "[C]================================\n" +
                                                "[L]Item" + "[C]Qty" + "[R]Rate" + "\n" +
                                                "[C]--------------------------------\n" +
                                                buffer.toString() +
                                                "[C]---------------------------------\n" +
                                                "[R]Total:" + "[R]" + totalCost + "\n" +
                                                "[C]Thank you for your visit\n"
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }).start();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ConfirmFinalBill.this, "Bluetooth Service is not granted", Toast.LENGTH_SHORT).show();
                }
            }

        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternetConn conn = new InternetConn(ConfirmFinalBill.this);
                if (conn.haveNetworkConnection()) {
                    paymentAlertDialog.setView(paymentView);
                    paymentAlertDialog.setCancelable(true);
                    cash_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            paymentAlertDialog.dismiss();
                            String cash = "Cash";
                            confirmFinalBill(cash);
                        }
                    });

                    online_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            paymentAlertDialog.dismiss();
                            String online = "Online";
                            confirmFinalBill(online);
                        }
                    });
                    paymentAlertDialog.show();
                } else {
                    Toast.makeText(ConfirmFinalBill.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogAddyourself.setTitle("Enter food details");
                alertDialogAddyourself.setIcon(R.drawable.nav_food_menu);
                alertDialogAddyourself.setView(addYourself);
                addFoodCost.setText("");
                addFoodTitle.setText("");
                addFoodQty.setText("");

                alertDialogAddyourself.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogAddyourself.dismiss();
                    }
                });

                alertDialogAddyourself.setButton(AlertDialog.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String getFoodQty = addFoodQty.getText().toString().trim();
                        String getFoodTitle = addFoodTitle.getText().toString().trim();
                        String getFoodCost = addFoodCost.getText().toString().trim();
                        if (getFoodCost.equals("") || getFoodQty.equals("") || getFoodTitle.equals("")) {
                            alertDialogAddyourself.dismiss();
                            Toast.makeText(ConfirmFinalBill.this, "All fields are compulsory", Toast.LENGTH_SHORT).show();
                        } else {
                            double food_cost = Double.parseDouble(getFoodCost);
                            int food_qty = Integer.parseInt(getFoodQty);
                            if (food_cost == 0.0 || food_qty == 0) {
                                alertDialogAddyourself.dismiss();
                                Toast.makeText(ConfirmFinalBill.this, "Cost/Qty cannot be zero!", Toast.LENGTH_SHORT).show();
                            } else {
                                FinalBillModel model = new FinalBillModel(getFoodTitle, food_cost, food_qty);
                                addToFinalBill(model);
                            }
                        }
                    }
                });
                alertDialogAddyourself.setCancelable(false);
                alertDialogAddyourself.show();
            }
        });

        adapter.setOnFinalBillItemTitleClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemTitleClick() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot, int pos) {
                //title isclicked
                editTitleET.setText("");
                editTitleET.setHint(snapshot.getString("item_title"));
                alertDialogTitle.setTitle("Edit Item Title");
                alertDialogTitle.setView(editTitleView);
                alertDialogTitle.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String itemT = editTitleET.getText().toString().trim();
                        if (itemT.equals(null) || itemT.equals("")) {
                            Toast.makeText(ConfirmFinalBill.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                        } else {
                            updateItemTitle(snapshot, itemT);
                        }
                    }
                });
                alertDialogTitle.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogTitle.dismiss();
                    }
                });
                alertDialogTitle.setCancelable(false);
                alertDialogTitle.show();

            }
        });

        adapter.setOnFinalBillItemCostClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemCostClick() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot, int pos) {
                //cost is clicked
                editCostET.setText("");
                editCostET.setHint(String.valueOf(snapshot.getDouble("item_cost") / snapshot.getDouble("item_qty").intValue()));
                alertDialogCost.setTitle("Edit Item Cost for 1 Qty");
                alertDialogCost.setView(editCostView);
                alertDialogCost.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String costString = editCostET.getText().toString().trim();
                        if (costString.equals(null) || costString.equals("")) {
                            Toast.makeText(ConfirmFinalBill.this, "Empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            double cost = Double.parseDouble(editCostET.getText().toString());
                            if (cost == 0.0 || cost == 0) {
                                Toast.makeText(ConfirmFinalBill.this, "Cost cannot be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                updateItemCost(snapshot, cost);
                            }
                        }
                    }
                });
                alertDialogCost.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogCost.dismiss();
                    }
                });
                alertDialogCost.setCancelable(false);
                alertDialogCost.show();
            }
        });

        adapter.setOnFinalBillItemQtyClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillItemQtyClick() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot, int pos) {
                //qty is clicked
                editqtyET.setText("");
                editqtyET.setHint(String.valueOf(snapshot.getDouble("item_qty").intValue()));
                alertDialogQty.setTitle("Edit Item Qty");
                alertDialogQty.setView(editqtyView);
                alertDialogQty.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editqtyET.getText().toString().trim().equals("") || editqtyET.getText().toString().trim().equals(null)) {
                            Toast.makeText(ConfirmFinalBill.this, "Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            int qty = Integer.parseInt(editqtyET.getText().toString());
                            if (qty == 0) {
                                Toast.makeText(ConfirmFinalBill.this, "Quantity is empty!", Toast.LENGTH_SHORT).show();
                            } else {
                                updateItemQty(snapshot, qty);
                            }
                        }
                    }
                });
                alertDialogQty.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogQty.dismiss();
                    }
                });
                alertDialogQty.setCancelable(false);
                alertDialogQty.show();
            }
        });

        adapter.setOnFinalBillDeleteClickListener(new ConfirmFinalBillFirestoreAdapter.onFinalBillDeleteClick() {
            @Override
            public void onItemClick(final DocumentSnapshot snapshot, final int pos) {
                //delete the item
                String itemTitle = snapshot.getString("item_title");
                alertDialogDelete.setTitle("Delete item!");
                alertDialogDelete.setMessage("Are you sure want to delete " + itemTitle + "?");
                alertDialogDelete.setIcon(R.drawable.ic_delete);
                alertDialogDelete.setCancelable(false);
                alertDialogDelete.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialogDelete.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateConfirmed_cost(snapshot, pos);
                    }

                    private void updateConfirmed_cost(final DocumentSnapshot snapshot, final int pos) {
                        final double cost_to_deduct = snapshot.getDouble("item_cost");
                        final DocumentReference parentRef = custRef.document(doc_id);
                        final DocumentReference confirmedKotRef = confirmedRef.document(snapshot.getId());
                        progressBar.setVisibility(View.VISIBLE);

                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot currentsnapshot = transaction.get(parentRef);
                                double confirmed_cost = currentsnapshot.getDouble("confirmed_cost");
                                double final_cost = confirmed_cost - cost_to_deduct;
                                transaction.update(parentRef, "confirmed_cost", final_cost);
                                transaction.delete(confirmedKotRef);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ConfirmFinalBill.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                updateTableCost();
                            }
                        });
                    }
                });
                alertDialogDelete.show();
            }
        });
    }

    private void addToFinalBill(final FinalBillModel model) {
        progressBar.setVisibility(View.VISIBLE);
        final double cost = model.getItem_cost();
        final DocumentReference confirmedKotRef = confirmedRef.document();
        final DocumentReference parentKotRef = custRef.document(doc_id);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(parentKotRef);
                double current_conf_cost = snapshot.getDouble("confirmed_cost");
                double total_cost = current_conf_cost + cost;
                transaction.set(confirmedKotRef, model);
                transaction.update(parentKotRef, "confirmed_cost", total_cost);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateTableCost();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ConfirmFinalBill.this, "Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItemCost(DocumentSnapshot snapshot, final double cost) {
        String id = snapshot.getId();
        int saved_qty = snapshot.getDouble("item_qty").intValue();
        double final_cost = cost * saved_qty;
        double saved_cost = snapshot.getDouble("item_cost");
        final double cost_difference = final_cost - saved_cost;

        progressBar.setVisibility(View.VISIBLE);

        confirmedRef.document(id).update("item_cost", final_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateConfCost(cost_difference);
                } else {
                    Toast.makeText(ConfirmFinalBill.this, "Failed to update the cost!", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateConfCost(final double cost_difference) {
                custRef.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            double confirmed_cost = task.getResult().getDouble("confirmed_cost");
                            final double f_cost = confirmed_cost + cost_difference;
                            custRef.document(doc_id).update("confirmed_cost", f_cost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        double roundedDouble = Math.round(f_cost * 100.0) / 100.0;
                                        totalCost.setText(String.valueOf(roundedDouble));
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ConfirmFinalBill.this, "Cost updated", Toast.LENGTH_SHORT).show();
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

    private void updateItemQty(DocumentSnapshot snapshot, final int qty) {
        //TODO UPDATE IN FINAL_BILL COST REMAINING
        progressBar.setVisibility(View.VISIBLE);
        final String id = snapshot.getId();
        double current_cost = snapshot.getDouble("item_cost");
        int current_qty = snapshot.getDouble("item_qty").intValue();
        double single_item_cost = current_cost / current_qty;
        final double new_cost = single_item_cost * qty;
        final double final_cost = new_cost - current_cost;

        final DocumentReference parentRef = custRef.document(doc_id);
        final DocumentReference confKotRef = confirmedRef.document(id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                double saved_cost = transaction.get(parentRef).getDouble("confirmed_cost");
                double f_Cost = saved_cost + final_cost;
                transaction.update(parentRef, "confirmed_cost", f_Cost);
                transaction.update(confKotRef, "item_qty", qty);
                transaction.update(confKotRef, "item_cost", new_cost);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateTableCost();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ConfirmFinalBill.this, "Quantity updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItemTitle(DocumentSnapshot snapshot, String newTitle) {
        String id = snapshot.getId();
        progressBar.setVisibility(View.VISIBLE);
        confirmedRef.document(id).update("item_title", newTitle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ConfirmFinalBill.this, "Title updated", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ConfirmFinalBill.this, "Failed to update the title", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupRecyclerView() {
        Query query = confirmedRef;
        FirestoreRecyclerOptions<FinalBillModel> cust = new FirestoreRecyclerOptions.Builder<FinalBillModel>()
                .setQuery(query, FinalBillModel.class)
                .build();
        adapter = new ConfirmFinalBillFirestoreAdapter(cust);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void fetchTableInfoTotalcost() {
        custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String table_no = String.valueOf(documentSnapshot.getLong("table_no").intValue());
                String table_type = documentSnapshot.getString("table_type");
                updateTableCost();
                tableNo.setText(table_no);
                tableType.setText(table_type);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tableNo.setText("Error!");
                tableType.setText("Error!");
            }
        });
    }

    private void updateTableCost() {
        custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double conf_cost = documentSnapshot.getDouble("confirmed_cost");
                double roundedDouble = Math.round(conf_cost * 100.0) / 100.0;
                String total_cost = String.valueOf(roundedDouble);
                totalCost.setText(total_cost);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                totalCost.setText("Error!");
            }
        });
    }


    private void confirmFinalBill(final String payment_mode) {
        progressBar.setVisibility(View.VISIBLE);

        custRef.document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final_confirmed_cost = task.getResult().getDouble("confirmed_cost");
                    table_no = task.getResult().getDouble("table_no").intValue();
                    no_of_cust = task.getResult().getDouble("no_of_cust").intValue();
                    table_type = task.getResult().getString("table_type");
                    date_time = task.getResult().getString("date_time");
                    current_costFinal = task.getResult().getDouble("current_cost");

                    if (final_confirmed_cost == 0.0 || final_confirmed_cost == 0) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ConfirmFinalBill.this, "There is nothing to confirm", Toast.LENGTH_SHORT).show();
                    } else {
                        savetoHistory(payment_mode);
                    }
                } else {
                }
            }

            private void savetoHistory(final String payment_mode) {
                HistoryModel model = new HistoryModel(date_time, payment_mode, final_confirmed_cost, table_type, table_no, Bill_NO, completed_date, no_of_cust);

                final WriteBatch batch1 = db.batch();
                final WriteBatch batch2 = db.batch();
                final WriteBatch batch3 = db.batch();
                final WriteBatch batch4 = db.batch();
                DocumentReference histDocRef = historyRef.document();
                batch1.set(histDocRef, model);
                final String history_doc_id = histDocRef.getId();
                batch1.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        confirmedRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    FinalBillModel model2 = snapshot.toObject(FinalBillModel.class);
                                    DocumentReference historyConfKotRef = historyRef.document(history_doc_id)
                                            .collection(CONFIRMED_KOT).document(snapshot.getId());
                                    batch2.set(historyConfKotRef, model2);
                                    batch2.delete(confirmedRef.document(snapshot.getId()));
                                }
                                batch2.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (current_costFinal != 0.0) {
                                            db.collection(CUSTOMERS).document(doc_id).collection(CURRENT_KOT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                                            batch4.delete(db.collection(CUSTOMERS).document(doc_id)
                                                                    .collection(CURRENT_KOT).document(snapshot1.getId()));
                                                        }
                                                        batch4.commit();
                                                    }
                                                }
                                            });
                                        }
                                        batch3.delete(custRef.document(doc_id));
                                        batch3.update(db.collection(TABLES).document(table_type), String.valueOf(table_no), true);
                                        batch3.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                addtoGrandTotal();
                                                if (payment_mode.equals("Online")) {
                                                    addToOnlineTotal();
                                                }
                                                addToTableWiseTotal();
                                                Toast.makeText(ConfirmFinalBill.this, "Order completed", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), Manager.class));
                                                overridePendingTransition(0, 0);
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });

    }

    private void addToTableWiseTotal() {
        final String date_only = date_time.substring(0, 8);
        final String month_only = date_time.substring(3, 8);
        Map<String, Object> datemap = new HashMap<>();
        datemap.put("date", date_only);
        final Map<String, Object> monthmap = new HashMap<>();
        monthmap.put("month", month_only);

        db.collection(TABLETALLYDAILY).document(date_only).set(datemap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.collection(TABLETALLYDAILY).document(date_only).collection(table_type)
                            .document(String.valueOf(table_no)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    final DocumentReference reference = db.collection(TABLETALLYDAILY).document(date_only).collection(table_type)
                                            .document(String.valueOf(table_no));
                                    db.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            //daily date is already available
                                            double stored_cost = transaction.get(reference).getDouble("tabletotal");
                                            double total_cost = stored_cost + final_confirmed_cost;
                                            TableTotalModel model = new TableTotalModel(total_cost, table_no);
                                            transaction.set(reference, model);
                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            addToMonthlyTable();
                                        }
                                    });

                                } else {
                                    //first daily entry
                                    TableTotalModel model = new TableTotalModel(final_confirmed_cost, table_no);
                                    db.collection(TABLETALLYDAILY).document(date_only).collection(table_type)
                                            .document(String.valueOf(table_no)).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                addToMonthlyTable();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }

            private void addToMonthlyTable() {

                db.collection(TABLETALLYMONTHLY).document(month_only).set(monthmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            db.collection(TABLETALLYMONTHLY).document(month_only).collection(table_type)
                                    .document(String.valueOf(table_no)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            final DocumentReference reference = db.collection(TABLETALLYMONTHLY).document(month_only).collection(table_type)
                                                    .document(String.valueOf(table_no));
                                            db.runTransaction(new Transaction.Function<Void>() {
                                                @Nullable
                                                @Override
                                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                    //daily date is already available
                                                    double stored_cost = transaction.get(reference).getDouble("tabletotal");
                                                    double total_cost = stored_cost + final_confirmed_cost;
                                                    TableTotalModel model = new TableTotalModel(total_cost, table_no);
                                                    transaction.set(reference, model);
                                                    return null;
                                                }
                                            });

                                        } else {
                                            //first daily entry
                                            TableTotalModel model = new TableTotalModel(final_confirmed_cost, table_no);
                                            db.collection(TABLETALLYMONTHLY).document(month_only).collection(table_type)
                                                    .document(String.valueOf(table_no)).set(model);
                                        }
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
        final String date_only = date_time.substring(0, 8);
        final String month_only = date_time.substring(3, 8);

        tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final DocumentReference reference = tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only);

                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                double stored_gt = transaction.get(reference).getDouble("onlinetotal");
                                double total_gt = stored_gt + final_confirmed_cost;
                                OnlineTotalModel model = new OnlineTotalModel(total_gt, date_only);
                                transaction.set(reference, model);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addtoOnlineMonthly();
                            }
                        });

                    } else {
                        OnlineTotalModel model = new OnlineTotalModel(final_confirmed_cost, date_only);
                        tallyRef.document(DAILY).collection(ONLINETOTAL).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
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
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                final DocumentReference reference = tallyRef.document(MONTHLY).collection(ONLINETOTAL).document(month_only);
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        //exists
                                        double stored_gt = transaction.get(reference).getDouble("onlinetotal");
                                        double total_gt = final_confirmed_cost + stored_gt;
                                        OnlineTotalModel model = new OnlineTotalModel(total_gt, month_only);
                                        transaction.set(reference, model);
                                        return null;
                                    }
                                });

                            } else {
                                //doesn't exist
                                OnlineTotalModel model = new OnlineTotalModel(final_confirmed_cost, month_only);
                                tallyRef.document(MONTHLY).collection(ONLINETOTAL).document(month_only).set(model);
                            }
                        }
                    }
                });


            }
        });
    }

    private void addtoGrandTotal() {
        final String date_only = date_time.substring(0, 8);
        final String month_only = date_time.substring(3, 8);


        tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final DocumentReference reference = tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                double stored_gt = transaction.get(reference).getDouble("grandtotal");
                                double total_gt = stored_gt + final_confirmed_cost;
                                GrandTotalModel model = new GrandTotalModel(total_gt, date_only);
                                transaction.set(reference, model);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addToGTMonthly();
                            }
                        });
                    } else {
                        GrandTotalModel model = new GrandTotalModel(final_confirmed_cost, date_only);
                        tallyRef.document(DAILY).collection(GRANDTOTAL).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
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
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                final DocumentReference reference = tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only);
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        //exists
                                        double stored_gt = transaction.get(reference).getDouble("grandtotal");
                                        double total_gt = final_confirmed_cost + stored_gt;
                                        GrandTotalModel model = new GrandTotalModel(total_gt, month_only);
                                        transaction.set(reference, model);
                                        return null;
                                    }
                                });

                            } else {
                                //doesn't exist
                                GrandTotalModel model = new GrandTotalModel(final_confirmed_cost, month_only);
                                tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only).set(model, SetOptions.merge());
                            }
                        }
                    }
                });
            }
        });
    }
}

