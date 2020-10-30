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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import ace.infosolutions.guruprasadhotelapp.Captain.ViewCart.ViewCartModel;
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

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UniCodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

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
                    Toast.makeText(PrintingMain.this, "Message1", Toast.LENGTH_SHORT).show();
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
                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));
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
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

    }// onCreate

    private void createParcelFinalBill(ParcelFinalBillPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());
        if (print.getCustomer_address().isEmpty()) {
            BILL = "";
            BILL = "                   GURUPRASAD HOTEL    \n"
                    + "                   Mahadevnagar, Urun Islampur" + "\n " +
                    "                   Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                    "                   Bill No:" + print.getBill_no() + "\n " +
                    "                   Parcel to - " + print.getCustomer_name() + "\n ";
            BILL = BILL
                    + "-----------------------------------------------\n";
            String format = "%1$4s %2$10s %3$10s%n";
            BILL = BILL + String.format(format, "Item", "Qty", "Amount");
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------";
            for (int i = 0; i < arrayList.size(); i++) {
                BILL = BILL + "\n " + String.format(format, arrayList.get(i).getItem_title(),
                        arrayList.get(i).getItem_qty(), arrayList.get(i).getItem_cost());
            }
            BILL = BILL
                    + "\n-----------------------------------------------\n";
            BILL = BILL +
                    "                          SUBTOTAL:" + print.getSubtotal() + "\n";
            BILL = BILL +
                    "                          DISCOUNT:" + print.getDiscount() + "\n";
            BILL = BILL +
                    "                           TOTAL:" + print.getTotal_cost() + "\n";
            BILL = BILL + "\n ";
            BILL = BILL
                    + "-----------------------------------------------\n";
            BILL = BILL + "\n\n ";
        } else {
            BILL = "";
            BILL = "                   GURUPRASAD HOTEL    \n"
                    + "                   Mahadevnagar, Urun Islampur" + "\n " +
                    "                   Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                    "                   Bill No:" + print.getBill_no() + "\n " +
                    "                   Parcel to - " + print.getCustomer_name() + "\n " +
                    "                   Address: " + print.getCustomer_address() + "\n ";

            BILL = BILL
                    + "-----------------------------------------------\n";
            String format = "%1$4s %2$10s %3$10s%n";
            BILL = BILL + String.format(format, "Item", "Qty", "Amount");
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------";
            for (int i = 0; i < arrayList.size(); i++) {
                BILL = BILL + "\n " + String.format(format, arrayList.get(i).getItem_title(),
                        arrayList.get(i).getItem_qty(), arrayList.get(i).getItem_cost());
            }
            BILL = BILL
                    + "\n-----------------------------------------------\n";
            BILL = BILL +
                    "                          SUBTOTAL:" + print.getSubtotal() + "\n";
            BILL = BILL +
                    "                          DISCOUNT:" + print.getDiscount() + "\n";
            BILL = BILL +
                    "                           TOTAL:" + print.getTotal_cost() + "\n";
            BILL = BILL + "\n";
            BILL = BILL
                    + "-----------------------------------------------\n";
            BILL = BILL + "\n\n ";
        }


    }

    private void createOrderFinalBill(OrderFinalBillPOJO orderFinalBillPOJO) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(orderFinalBillPOJO.getArrayList());

        BILL = "";
        BILL = "                   GURUPRASAD HOTEL    \n"
                + "                   Mahadevnagar, Urun Islampur" + "\n " +
                "                   Date&Time:" + orderFinalBillPOJO.getDate() + " " + orderFinalBillPOJO.getTime() + "\n " +
                "                   Bill No:" + orderFinalBillPOJO.getBill_no() + "\n " +
                "                   Table No:" + orderFinalBillPOJO.getTable_no() + " (" + orderFinalBillPOJO.getTable_type() + ")" + "\n ";

        BILL = BILL
                + "-----------------------------------------------\n";
        String format = "%1$4s %2$10s %3$10s%n";
        BILL = BILL + String.format(format, "Item", "Qty", "Amount");
        BILL = BILL + "\n";
        BILL = BILL
                + "-----------------------------------------------";
        for (int i = 0; i < arrayList.size(); i++) {
            BILL = BILL + "\n " + String.format(format, arrayList.get(i).getItem_title(),
                    arrayList.get(i).getItem_qty(), arrayList.get(i).getItem_cost());
        }
        BILL = BILL
                + "\n-----------------------------------------------\n";
        BILL = BILL +
                "                          SUBTOTAL:" + orderFinalBillPOJO.getSubtotal() + "\n";
        BILL = BILL +
                "                          DISCOUNT:" + orderFinalBillPOJO.getDiscount() + "\n";
        BILL = BILL +
                "                           TOTAL:" + orderFinalBillPOJO.getTotal_cost() + "\n";
        BILL = BILL + "\n ";
        BILL = BILL
                + "-----------------------------------------------\n";
        BILL = BILL
                + "------Thanks for the visit-----";
        BILL = BILL + "\n\n";
    }

    private void createParcelKOT(ParcelKOTPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());
        BILL = "";
        BILL = "                   GURUPRASAD HOTEL    \n"
                + "                   KOT NO:" + print.getKot_no() + "\n " +
                "                   Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                "                   Parcel - " + print.getCustomer_name() + "\n ";
        BILL = BILL
                + "-----------------------------------------------\n";


        String format = "%1$4s %2$10s";
        BILL = BILL + String.format(format, "Item", "Qty");
        BILL = BILL + "\n";
        BILL = BILL
                + "-----------------------------------------------";
        for (int i = 0; i < arrayList.size(); i++) {
            BILL = BILL + "\n " + String.format(format, arrayList.get(i).getItem_title(),
                    arrayList.get(i).getItem_qty());
        }

        BILL = BILL
                + "\n-----------------------------------------------";
        BILL = BILL + "\n\n ";
    }

    private void createOrderKot(OrderKOTPOJO print) {
        ArrayList<ViewCartModel> arrayList = new ArrayList<>();
        arrayList.addAll(print.getArrayList());
        BILL = "";
        BILL = "                   GURUPRASAD HOTEL    \n"
                + "                   KOT NO:" + print.getKot_no() + "\n " +
                "                   Date&Time:" + print.getDate() + " " + print.getTime() + "\n " +
                "                   Table No:" + print.getTable_no() + " (" + print.getTable_type() + ")" + "\n ";

        BILL = BILL
                + "-----------------------------------------------\n";
        String format = "%1$4s %2$10s";
        BILL = BILL + String.format(format, "Item", "Qty");
        BILL = BILL + "\n";
        BILL = BILL
                + "-----------------------------------------------";
        for (int i = 0; i < arrayList.size(); i++) {
            BILL = BILL + "\n " + String.format(format, arrayList.get(i).getItem_title(),
                    arrayList.get(i).getItem_qty());
        }
        BILL = BILL
                + "\n-----------------------------------------------";
        BILL = BILL + "\n\n ";
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

}
