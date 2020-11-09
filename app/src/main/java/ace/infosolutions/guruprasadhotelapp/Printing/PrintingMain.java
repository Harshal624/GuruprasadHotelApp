package ace.infosolutions.guruprasadhotelapp.Printing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
import ace.infosolutions.guruprasadhotelapp.Printing.POJOs.OrderFinalBillPOJO;
import ace.infosolutions.guruprasadhotelapp.Printing.POJOs.OrderKOTPOJO;
import ace.infosolutions.guruprasadhotelapp.Printing.POJOs.ParcelFinalBillPOJO;
import ace.infosolutions.guruprasadhotelapp.Printing.POJOs.ParcelKOTPOJO;
import ace.infosolutions.guruprasadhotelapp.R;

import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PREF_DOCID;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.PrintingPOJOConstant;
import static ace.infosolutions.guruprasadhotelapp.Utils.Constants.SP_PRINT_TYPE;

public class PrintingMain extends Activity implements Runnable {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    final int MAX_NO_OF_CHAR = 11;
    final int MAX_NO_OF_CHAR2 = 15;
    String BILL = "";
    private SharedPreferences sharedPreferences;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrintingMain.this, "Device connected", Toast.LENGTH_SHORT).show();
        }
    };

   /* public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UniCodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }*/

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.printing_main);
        mScan = findViewById(R.id.Scan);
        sharedPreferences = getSharedPreferences(PREF_DOCID, MODE_PRIVATE);
        String print_type = sharedPreferences.getString(SP_PRINT_TYPE, "");
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PrintingPOJOConstant, "");
        if (print_type.equals("order_kot")) {
            OrderKOTPOJO orderKOTPOJO = gson.fromJson(json, OrderKOTPOJO.class);
            createOrderKot(orderKOTPOJO);
        } else if (print_type.equals("parcel_kot")) {
            ParcelKOTPOJO parcelKOTPOJO = gson.fromJson(json, ParcelKOTPOJO.class);
            createParcelKOT(parcelKOTPOJO);
        } else if (print_type.equals("order_bill")) {
            OrderFinalBillPOJO orderFinalBillPOJO = gson.fromJson(json, OrderFinalBillPOJO.class);
            createOrderFinalBill(orderFinalBillPOJO);
        } else if (print_type.equals("parcel_bill")) {
            ParcelFinalBillPOJO parcelFinalBillPOJO = gson.fromJson(json, ParcelFinalBillPOJO.class);
            createParcelFinalBill(parcelFinalBillPOJO);
        }
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PrintingMain.this, "Unavailable", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintingMain.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            os.write(BILL.getBytes());
                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });

        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.disable();
                    Toast.makeText(PrintingMain.this, "Disconnected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrintingMain.this, "Device is not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }// onCreate

    private void createParcelFinalBill(ParcelFinalBillPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());
        if (print.getCustomer_address().isEmpty()) {


            BILL = "              GURPRASAD HOTEL\n" +
                    "             Mahadevnager,Islampur\n\n" +
                    "                    Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                    "                   BILL NO:" + print.getBill_no() + "\n " +
                    "                   Parcel to - " + print.getCustomer_name() + "\n ";
            BILL = BILL +
                    "-------------------------------------------\n";

            BILL = BILL + String.format("%1$-10s %2$5s %3$10s %4$5s %5$10s", "  Item", " ", "Qty", "  ", "Total\n");
            BILL = BILL +
                    "---------------------------------------------\n";

            for (int i = 0; i < arrayList.size(); i++) {
                String title = arrayList.get(i).getItem_title();
                if (title.length() > 11) {
                    title = title.substring(0, 11);
                } else {
                    int length = title.length();
                    int flength = 11 - length;
                    for (int j = 0; j < flength; j++) {
                        title = title + " ";
                    }
                }
                BILL = BILL + String.format("%1$-5s %2$5s %3$10s %4$5s %5$10s", title, " ", arrayList.get(i).getItem_qty(),
                        " ", arrayList.get(i).getItem_cost() + "\n");
            }


            BILL = BILL +
                    "\n---------------------------------------------\n";
            BILL = BILL + "\n ";

            BILL = BILL + "                            Subtotal:" + "" + print.getSubtotal() + "\n";
            BILL = BILL + "                             Discount:" + "" + print.getDiscount() + "\n";
            BILL = BILL + "                     " +
                    "        Total Value:" + "" + print.getTotal_cost() + "\n";

            BILL = BILL +
                    "------------------------------------------------\n";
            BILL = BILL + "\n\n";
            // Log.e("BILLDESIGN",BILL);
        } else {

            BILL = "              GURPRASAD HOTEL\n" +
                    "             Mahadevnager,Islampur\n\n" +
                    "                    Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                    "                   BILL NO:" + print.getBill_no() + "\n " +
                    "                   Parcel to - " + print.getCustomer_name() + "\n " +
                    " Address: " + print.getCustomer_address() + "\n ";
            BILL = BILL +
                    "-------------------------------------------\n";

            BILL = BILL + String.format("%1$-10s %2$5s %3$10s %4$5s %5$10s", "  Item", " ", "Qty", "  ", "Total\n");
            BILL = BILL +
                    "---------------------------------------------\n";

            for (int i = 0; i < arrayList.size(); i++) {
                String title = arrayList.get(i).getItem_title();
                if (title.length() > 11) {
                    title = title.substring(0, 11);
                } else {
                    int length = title.length();
                    int flength = 11 - length;
                    for (int j = 0; j < flength; j++) {
                        title = title + " ";
                    }
                }
                BILL = BILL + String.format("%1$-5s %2$5s %3$10s %4$5s %5$10s", title, " ", arrayList.get(i).getItem_qty(),
                        " ", arrayList.get(i).getItem_cost() + "\n");
            }


            BILL = BILL +
                    "\n---------------------------------------------\n";
            BILL = BILL + "\n ";

            BILL = BILL + "                            Subtotal:" + "" + print.getSubtotal() + "\n";
            BILL = BILL + "                             Discount:" + "" + print.getDiscount() + "\n";
            BILL = BILL + "                     " +
                    "        Total Value:" + "" + print.getTotal_cost() + "\n";

            BILL = BILL +
                    "------------------------------------------------\n";
            BILL = BILL + "\n\n";
            //  Log.e("BILLDESIGN",BILL);
        }
    }

    private void createOrderFinalBill(OrderFinalBillPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());

        BILL = "              GURPRASAD HOTEL\n" +
                "             Mahadevnager,Islampur\n\n" +
                "                    Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                "                   BILL NO:" + print.getBill_no() + "\n " +
                "                   Table No:" + print.getTable_no() + " (" + print.getTable_type() + ")" + "\n ";
        BILL = BILL +
                "-------------------------------------------\n";

        BILL = BILL + String.format("%1$-10s %2$5s %3$10s %4$5s %5$10s", "Item", " ", "Qty", " ", "Total\n");
        BILL = BILL +
                "---------------------------------------------\n";

        for (int i = 0; i < arrayList.size(); i++) {
            String title = arrayList.get(i).getItem_title();
            if (title.length() > 11) {
                title = title.substring(0, 11);
            } else {
                int length = title.length();
                int flength = MAX_NO_OF_CHAR - length;
                for (int j = 0; j < flength; j++) {
                    title = title + " ";
                }
            }
            BILL = BILL + String.format("%1$-5s %2$5s %3$10s %4$5s %5$10s", title, " ", arrayList.get(i).getItem_qty(),
                    " ", arrayList.get(i).getItem_cost() + "\n");
        }


        BILL = BILL +
                "\n---------------------------------------------\n";
        BILL = BILL + "\n ";

        BILL = BILL + "                            Subtotal:" + "" + print.getSubtotal() + "\n";
        BILL = BILL + "                             Discount:" + "" + print.getDiscount() + "\n";
        BILL = BILL + "                     " +
                "        Total Value:" + "" + print.getTotal_cost() + "\n";

        BILL = BILL +
                "------------------------------------------------\n";
        BILL = BILL + "\n\n";
        // Log.e("BILLDESIGN",BILL);
    }

    private void createParcelKOT(ParcelKOTPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());

        BILL = "              GURPRASAD HOTEL\n" +
                "                    Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                "                   KOT NO:" + print.getKot_no() + "\n " +
                "                   Parcel - " + print.getCustomer_name() + "\n ";
        BILL = BILL +
                "-------------------------------------------\n";

        BILL = BILL + String.format("%1$-10s %2$5s %3$10s", "Item", "               ", "Qty" + "\n");
        BILL = BILL +
                "---------------------------------------------\n";

        for (int i = 0; i < arrayList.size(); i++) {
            String title = arrayList.get(i).getItem_title();
            if (title.length() > 11) {
                title = title.substring(0, 11);
            } else {
                int length = title.length();
                int flength = 11 - length;
                for (int j = 0; j < flength; j++) {
                    title = title + " ";
                }
            }
            BILL = BILL + String.format("%1$-5s %2$5s %3$10s", title, "               ", arrayList.get(i).getItem_qty() + "\n");
        }


        BILL = BILL +
                "---------------------------------------------\n";
        BILL = BILL + "\n\n";
        // Log.e("BILLDESIGN",BILL);
    }

    private void createOrderKot(OrderKOTPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());

        BILL = "              GURPRASAD HOTEL\n" +
                "                    Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                "                   KOT NO:" + print.getKot_no() + "\n " +
                "                   Table No:" + print.getTable_no() + " (" + print.getTable_type() + ")" + "\n ";
        BILL = BILL +
                "-------------------------------------------\n";

        BILL = BILL + String.format("%1$-10s %2$5s %3$10s", "Item", "               ", "Qty" + "\n");
        BILL = BILL +
                "---------------------------------------------\n";

        for (int i = 0; i < arrayList.size(); i++) {
            String title = arrayList.get(i).getItem_title();
            if (title.length() > 11) {
                title = title.substring(0, 11);
            } else {
                int length = title.length();
                int flength = 11 - length;
                for (int j = 0; j < flength; j++) {
                    title = title + " ";
                }
            }
            BILL = BILL + String.format("%1$-5s %2$5s %3$10s", title, "             ", arrayList.get(i).getItem_qty() + "\n");
        }


        BILL = BILL +
                "---------------------------------------------\n";
        BILL = BILL + "\n\n";
        // Log.e("BILLDESIGN",BILL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintingMain.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintingMain.this, "Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

  /*  public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }*/
}

