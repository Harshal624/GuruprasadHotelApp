package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
import ace.infosolutions.guruprasadhotelapp.Manager.Manager;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.GrandTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.HistoryModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.OnlineTotalModel;
import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.TableTotalModel;
import ace.infosolutions.guruprasadhotelapp.Printing.POJOs.OrderFinalBillPOJO;
import ace.infosolutions.guruprasadhotelapp.R;
import ace.infosolutions.guruprasadhotelapp.Utils.Constants;
import ace.infosolutions.guruprasadhotelapp.Utils.GenerateNumber;
import ace.infosolutions.guruprasadhotelapp.Utils.InternetConn;

import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.DISCOUNT_TALLY;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PREF_DOCID;

public class ConfirmFinalBill extends AppCompatActivity implements Runnable {
    //Strings for Tally
    public static final String TALLY = "TALLY";
    public static final String DAILY = "Daily";
    public static final String MONTHLY = "Monthly";
    public static final String HISTORY = "HISTORY";
    public static final String TABLETALLYDAILY = "TABLETALLYDAILY";
    public static final String GRANDTOTAL = "GRANDTOTAL";
    public static final String TABLETALLYMONTHLY = "TABLETALLYMONTHLY";
    private static final int MAX_NO_OF_CHAR = 11;
    private static final String CUSTOMERS = "CUSTOMERS";
    private static final String TABLES = "Tables";
    private static final String CONFIRMED_KOT = "CONFIRMED_KOT";
    private static final String CURRENT_KOT = "CURRENT_KOT";
    private final String DEVICE_ADDRESS = "02:3D:EE:0D:CF:E8";
    OutputStream mmOutputStream;
    GenerateNumber number = new GenerateNumber();
    //
    RadioButton type;
    String discount_type = "Amount";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String BILL;
    private String completed_date;
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
    private String table_type, time_arrived, time_completed, date_arrived;
    private int table_no, no_of_cust;
    private ProgressBar progressBar;
    private double final_confirmed_cost, total_cost_final, discount_final;
    private Button payment;
    private AlertDialog paymentAlertDialog;
    private View paymentView;
    private TextView online_payment, cash_payment;
    private double current_costFinal;

    private SharedPreferences sharedPreferences;
    private TextView discount_textview, total_cost_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_bill);
        db = FirebaseFirestore.getInstance();
        //
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        //

        sharedPreferences = getSharedPreferences(PREF_DOCID, MODE_PRIVATE);
        recyclerView = findViewById(R.id.final_bill_recycler);
        doc_id = getIntent().getStringExtra("FinalDOCID");
        addItem = findViewById(R.id.add_item);
        payment = findViewById(R.id.payment);
        discount_textview = findViewById(R.id.discount);
        total_cost_textview = findViewById(R.id.total);
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

        time_completed = number.generateTimeOnly();
        completed_date = number.generateDateOnly();

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
        Button apply_discount = findViewById(R.id.apply_discount);
        apply_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1 = LayoutInflater.from(ConfirmFinalBill.this).inflate(R.layout.discountview_alert, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFinalBill.this);
                final AlertDialog alertDialog = builder.create();
                final TextView subtotal = view1.findViewById(R.id.subtotal);
                final EditText discount_enter = view1.findViewById(R.id.discount_amount);
                discount_enter.setHint("Rs.");
                RadioGroup radioGroup = view1.findViewById(R.id.radiogroup);
                custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        subtotal.setText(String.valueOf(snapshot.getDouble("confirmed_cost")));
                    }
                });
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        type = view1.findViewById(i);
                        discount_type = type.getText().toString();
                        if (discount_type.equals("Amount")) {
                            discount_enter.setHint("Rs.");
                        } else {
                            discount_enter.setHint("%");
                        }
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (discount_enter.getText().toString().trim().equals("") || discount_enter.getText().toString().trim().equals(null)) {
                            alertDialog.dismiss();
                            Toast.makeText(ConfirmFinalBill.this, "No value entered", Toast.LENGTH_SHORT).show();
                        } else {
                            double discount = Double.parseDouble(discount_enter.getText().toString().trim());
                            if (discount == 0.0) {
                                alertDialog.dismiss();
                                Toast.makeText(ConfirmFinalBill.this, "No discount added", Toast.LENGTH_SHORT).show();
                            } else {
                                alertDialog.dismiss();
                                progressBar.setVisibility(View.VISIBLE);
                                applyDiscount(discount);
                            }
                        }
                    }

                    private void applyDiscount(final double discount) {
                        final DocumentReference reference = custRef.document(doc_id);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                double subtotal = transaction.get(reference).getDouble("confirmed_cost");
                                double total;
                                if (discount_type.equals("Amount")) {
                                    total = subtotal - discount;
                                    transaction.update(reference, "discount", discount);
                                } else {
                                    double perc = ((discount / 100) * subtotal);
                                    total = subtotal - perc;
                                    transaction.update(reference, "discount", perc);
                                }
                                transaction.update(reference, "total_cost", total);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                updateTableCost();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ConfirmFinalBill.this, "Discount applied", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(view1);
                alertDialog.show();
            }
        });

        printBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                printFinalBill();
            }

            private void printFinalBill() {
                //print final bill
                final ArrayList<ViewCartModel> arrayList = new ArrayList<>();
                final String date = number.generateDateOnly();
                final String time = number.generateTimeOnly();

                custRef.document(doc_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        final String table_no = String.valueOf(snapshot.getDouble("table_no").intValue());
                        final String table_type = snapshot.getString("table_type");
                        double conf_cost = snapshot.getDouble("confirmed_cost");
                        final String subtotal = String.valueOf(Math.round(conf_cost * 100.0 / 100.0));
                        final double disc = snapshot.getDouble("discount");
                        final String discount = String.valueOf(Math.round(disc * 100.0) / 100.0);
                        double total_cost = snapshot.getDouble("total_cost");
                        final String total_cost_string = String.valueOf(Math.round(total_cost * 100.0) / 100.0);
                        final String bill_no = snapshot.getString("bill_no");

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
                                            String item_Title = snapshot.getString("item_title_english");
                                            int item_Qty = snapshot.getDouble("item_qty").intValue();
                                            ViewCartModel getModel = new ViewCartModel(item_Title, item_Cost, item_Qty);
                                            if (getModel != null) {
                                                arrayList.add(getModel);
                                            }
                                        }
                                        if (!arrayList.isEmpty()) {
                                            OrderFinalBillPOJO orderFinalBillPOJO = new
                                                    OrderFinalBillPOJO(bill_no, date
                                                    , time, table_no, table_type, subtotal, discount, total_cost_string, arrayList);
                                            setUpBillString(arrayList, orderFinalBillPOJO);
                                            if (bluetoothAdapter == null) {
                                                Toast.makeText(ConfirmFinalBill.this, "Unavailable", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (!bluetoothAdapter.isEnabled()) {
                                                    Intent enableBtIntent = new Intent(
                                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                    startActivityForResult(enableBtIntent,
                                                            2);
                                                } else {
                                                    Thread thread = new Thread(ConfirmFinalBill.this);
                                                    thread.start();
                                                }
                                            }

                                            /*OrderFinalBillPOJO orderFinalBillPOJO = new
                                                    OrderFinalBillPOJO(bill_no, date
                                                    , time, table_no, table_type, subtotal, discount, total_cost_string, arrayList);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(orderFinalBillPOJO);
                                            editor.putString(PrintingPOJOConstant,json);
                                            editor.putString(SP_PRINT_TYPE, "order_bill");
                                            editor.commit();
                                            startActivity(new Intent(ConfirmFinalBill.this, PrintingMain.class));*/
                                        }
                                    }
                                }
                            });
                        }
                    }
                });


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

                final EditText food_title_english = addYourself.findViewById(R.id.food_item_title_english);
                food_title_english.setText("");

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
                        String getFoodTitleEnglish = food_title_english.getText().toString().trim();
                        if (getFoodCost.equals("") || getFoodQty.equals("") || getFoodTitle.equals("") ||
                                getFoodTitleEnglish.isEmpty()) {
                            alertDialogAddyourself.dismiss();
                            Toast.makeText(ConfirmFinalBill.this, "All fields are compulsory", Toast.LENGTH_SHORT).show();
                        } else {
                            double food_cost = Double.parseDouble(getFoodCost);
                            int food_qty = Integer.parseInt(getFoodQty);
                            if (food_cost == 0.0 || food_qty == 0) {
                                alertDialogAddyourself.dismiss();
                                Toast.makeText(ConfirmFinalBill.this, "Cost/Qty cannot be zero!", Toast.LENGTH_SHORT).show();
                            } else {
                                FinalBillModel model = new FinalBillModel(getFoodTitle, food_cost, food_qty, getFoodTitleEnglish);
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

    private void setUpBillString(ArrayList<ViewCartModel> arrayList, OrderFinalBillPOJO print) {
        double sbtotal = Math.round(Double.parseDouble(print.getSubtotal()) * 100.0) / 100.0;
        double dscount = Math.round(Double.parseDouble(print.getDiscount()) * 100.0) / 100.0;
        double ttlcost = Math.round(Double.parseDouble(print.getTotal_cost()) * 100.0) / 100.0;
        String time = print.getTime().substring(0, 5);
        BILL = "              GURPRASAD HOTEL\n" +
                "             Mahadevnagar,Islampur\n" +
                "             Contact: 9890845408\n\n" +
                "                    Date&Time:" + print.getDate() + " " + time + "\n " +
                "                   BILL NO:" + print.getBill_no() + "\n " +
                "                   Table No:" + print.getTable_no() + " (" + print.getTable_type() + ")" + "\n ";
        BILL = BILL +
                "-------------------------------------------\n";

        BILL = BILL + String.format("%1$-10s %2$10s %3$10s %4$10s", "Item", "Qty", "Rate", "Total\n");
        BILL = BILL +
                "---------------------------------------------\n";

        for (int i = 0; i < arrayList.size(); i++) {
            String title = arrayList.get(i).getItem_title();
            double rate = arrayList.get(i).getItem_cost() / arrayList.get(i).getItem_qty();
            double roundedRate = Math.round(rate * 100.0) / 100.0;
            double itemcst = Math.round(arrayList.get(i).getItem_cost() * 100.0) / 100.0;
            if (title.length() > 11) {
                title = title.substring(0, 11);
            } else {
                int length = title.length();
                int flength = MAX_NO_OF_CHAR - length;
                for (int j = 0; j < flength; j++) {
                    title = title + " ";
                }
            }
            BILL = BILL + String.format("%1$-10s %2$10s %3$10s %4$10s", title, arrayList.get(i).getItem_qty(), roundedRate
                    , itemcst + "\n");
        }


        BILL = BILL +
                "---------------------------------------------\n";

        BILL = BILL + "                            Subtotal:" + "" + sbtotal + "\n";
        BILL = BILL + "                            Discount:" + "" + dscount + "\n";
        BILL = BILL + "                     " +
                "       Total Value:" + "" + ttlcost + "\n";

        BILL = BILL +
                "-----------Thank you for your visit-----------\n";
        BILL = BILL + "\n";
        // Log.e("BILLFORMAT",BILL);
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
                transaction.update(parentKotRef, "total_cost", total_cost);
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
        final double final_cost = cost * saved_qty;
        double saved_cost = snapshot.getDouble("item_cost");
        final double cost_difference = final_cost - saved_cost;

        progressBar.setVisibility(View.VISIBLE);

        final DocumentReference reference = custRef.document(doc_id);
        final DocumentReference reference1 = confirmedRef.document(id);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                double confirmed_cost = transaction.get(reference).getDouble("confirmed_cost");
                final double f_cost = confirmed_cost + cost_difference;
                transaction.update(reference1, "item_cost", final_cost);
                transaction.update(reference, "confirmed_cost", f_cost);
                transaction.update(reference, "total_cost", f_cost);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateTableCost();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ConfirmFinalBill.this, "Cost updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItemQty(DocumentSnapshot snapshot, final int qty) {
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
                transaction.update(parentRef, "total_cost", f_Cost);
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
                double discount = documentSnapshot.getDouble("discount");
                double total = documentSnapshot.getDouble("total_cost");
                String total_discount = String.valueOf(Math.round(discount * 100.0) / 100.0);
                discount_textview.setText(total_discount);
                String t_cost = String.valueOf(Math.round(total * 100.0) / 100.0);
                total_cost_textview.setText(t_cost);
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
                    total_cost_final = task.getResult().getDouble("total_cost");
                    discount_final = task.getResult().getDouble("discount");
                    table_no = task.getResult().getDouble("table_no").intValue();
                    no_of_cust = task.getResult().getDouble("no_of_cust").intValue();
                    table_type = task.getResult().getString("table_type");
                    date_arrived = task.getResult().getString("date_arrived");
                    time_arrived = task.getResult().getString("time_arrived");
                    current_costFinal = task.getResult().getDouble("current_cost");
                    String bill_no = task.getResult().getString("bill_no");

                    if (final_confirmed_cost == 0.0 || final_confirmed_cost == 0) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ConfirmFinalBill.this, "There is nothing to confirm", Toast.LENGTH_SHORT).show();
                    } else {
                        savetoHistory(payment_mode, bill_no);
                    }
                } else {
                }
            }

            private void savetoHistory(final String payment_mode, final String bill_no) {
                HistoryModel model = new HistoryModel(date_arrived, completed_date, time_arrived, time_completed, payment_mode, final_confirmed_cost, discount_final, total_cost_final, table_type, table_no, no_of_cust, bill_no);

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
                                                addDiscountTotal();
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
        final String date_only = completed_date;
        final String month_only = completed_date.substring(3, 8);

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
                                            double total_cost = stored_cost + total_cost_final;
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
                                    TableTotalModel model = new TableTotalModel(total_cost_final, table_no);
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
                                                    double total_cost = stored_cost + total_cost_final;
                                                    TableTotalModel model = new TableTotalModel(total_cost, table_no);
                                                    transaction.set(reference, model);
                                                    return null;
                                                }
                                            });

                                        } else {
                                            //first daily entry
                                            TableTotalModel model = new TableTotalModel(total_cost_final, table_no);
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
        final String date_only = completed_date;
        final String month_only = completed_date.substring(3, 8);

        tallyRef.document(DAILY).collection(Constants.ONLINETOTAL).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final DocumentReference reference = tallyRef.document(DAILY).collection(Constants.ONLINETOTAL).document(date_only);

                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                double stored_gt = transaction.get(reference).getDouble("onlinetotal");
                                double total_gt = stored_gt + total_cost_final;
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
                        OnlineTotalModel model = new OnlineTotalModel(total_cost_final, date_only);
                        tallyRef.document(DAILY).collection(Constants.ONLINETOTAL).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                tallyRef.document(MONTHLY).collection(Constants.ONLINETOTAL).document(month_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                final DocumentReference reference = tallyRef.document(MONTHLY).collection(Constants.ONLINETOTAL).document(month_only);
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        //exists
                                        double stored_gt = transaction.get(reference).getDouble("onlinetotal");
                                        double total_gt = total_cost_final + stored_gt;
                                        OnlineTotalModel model = new OnlineTotalModel(total_gt, month_only);
                                        transaction.set(reference, model);
                                        return null;
                                    }
                                });

                            } else {
                                //doesn't exist
                                OnlineTotalModel model = new OnlineTotalModel(total_cost_final, month_only);
                                tallyRef.document(MONTHLY).collection(Constants.ONLINETOTAL).document(month_only).set(model);
                            }
                        }
                    }
                });


            }
        });
    }

    private void addtoGrandTotal() {
        final String date_only = completed_date;
        final String month_only = completed_date.substring(3, 8);


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
                                double total_gt = stored_gt + total_cost_final;
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
                        GrandTotalModel model = new GrandTotalModel(total_cost_final, date_only);
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
                                        double total_gt = total_cost_final + stored_gt;
                                        GrandTotalModel model = new GrandTotalModel(total_gt, month_only);
                                        transaction.set(reference, model);
                                        return null;
                                    }
                                });

                            } else {
                                //doesn't exist
                                GrandTotalModel model = new GrandTotalModel(total_cost_final, month_only);
                                tallyRef.document(MONTHLY).collection(GRANDTOTAL).document(month_only).set(model, SetOptions.merge());
                            }
                        }
                    }
                });
            }
        });
    }


    private void addDiscountTotal() {
        final String date_only = completed_date;
        final String month_only = completed_date.substring(3, 8);

        tallyRef.document(DAILY).collection(DISCOUNT_TALLY).document(date_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final DocumentReference reference = tallyRef.document(DAILY).collection(DISCOUNT_TALLY).document(date_only);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                double stored_gt = transaction.get(reference).getDouble("grandtotal");
                                double total_gt = stored_gt + discount_final;
                                GrandTotalModel model = new GrandTotalModel(total_gt, date_only);
                                transaction.set(reference, model);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addTODiscountMonthly();
                            }
                        });
                    } else {
                        GrandTotalModel model = new GrandTotalModel(discount_final, date_only);
                        tallyRef.document(DAILY).collection(DISCOUNT_TALLY).document(date_only).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addTODiscountMonthly();
                                }
                            }
                        });
                    }
                }
            }

            private void addTODiscountMonthly() {
                tallyRef.document(MONTHLY).collection(DISCOUNT_TALLY).document(month_only).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                final DocumentReference reference = tallyRef.document(MONTHLY).collection(DISCOUNT_TALLY).document(month_only);
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        //exists
                                        double stored_gt = transaction.get(reference).getDouble("grandtotal");
                                        double total_gt = discount_final + stored_gt;
                                        GrandTotalModel model = new GrandTotalModel(total_gt, month_only);
                                        transaction.set(reference, model);
                                        return null;
                                    }
                                });

                            } else {
                                //doesn't exist
                                GrandTotalModel model = new GrandTotalModel(discount_final, month_only);
                                tallyRef.document(MONTHLY).collection(DISCOUNT_TALLY).document(month_only).set(model, SetOptions.merge());
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2 && requestCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Bluetooth turned on successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void run() {
        try {
            socket = bluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            bluetoothAdapter.cancelDiscovery();
            socket.connect();
            mmOutputStream = socket.getOutputStream();
            printReceipt();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printReceipt() throws IOException {
        try {
            mmOutputStream.write(BILL.getBytes());//Charset.forName("UTF-8")
            mmOutputStream.write(new byte[]{0x1D, 0x56, 66, 0x00});


        } catch (Exception e) {
            //Toast.makeText(getContext(), "Printer is still loading, try again...", Toast.LENGTH_SHORT).show();
            //Log.e("MainActivity", "Exe ", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }
}

