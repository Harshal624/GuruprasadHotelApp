package ace.infosolutions.guruprasadhotelapp.Captain;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ace.infosolutions.guruprasadhotelapp.R;

public class ItemAlertDialog extends DialogFragment {

    private String item_title;
    private String item_cost;
    private String item_title_english;
    private double final_price;
    private int final_qty;
    private ItemAlertDialogListener listener;


    public ItemAlertDialog(String item_title, String item_cost, String item_title_english) {
        this.item_title = item_title;
        this.item_cost = item_cost;
        this.item_title_english = item_title_english;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final TextView item_costTV, counter;
        ImageButton incr_couter, decr_counter;
        final int[] count = {1};


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.itemdetails_alertdialog, null);
        //casting views
        item_costTV = view.findViewById(R.id.item_cost);
        decr_counter = view.findViewById(R.id.decrement_counter);
        incr_couter = view.findViewById(R.id.increment_counter);
        counter = view.findViewById(R.id.counter);

        //if nothing is seletected, intialize quantity and cost
        final_price = Double.parseDouble(item_cost.substring(3, item_cost.length()));
        final_qty = 1;
        //

        item_costTV.setText(item_cost);
        incr_couter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0]++;
                if (count[0] > 10) {
                    count[0] = 10;
                } else {
                    String itemCost = item_cost.substring(3, item_cost.length());
                    double final_cost = count[0] * Double.parseDouble(itemCost);
                    item_costTV.setText("Rs." + final_cost);
                    counter.setText("" + count[0]);
                    final_price = final_cost;
                    final_qty = count[0];
                }
            }
        });

        decr_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0]--;
                if (count[0] < 1) {
                    count[0] = 1;
                } else {
                    String itemCost = item_cost.substring(3, item_cost.length());
                    int final_cost = count[0] * Integer.parseInt(itemCost);
                    item_costTV.setText("Rs." + final_cost);
                    counter.setText("" + count[0]);
                    final_price = final_cost;
                    final_qty = count[0];
                }
            }
        });


        builder.setView(view)
                .setTitle(item_title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int qty = final_qty;
                        double cost = final_price;
                        listener.applyText(item_title, cost, qty, item_title_english);
                    }
                }).setCancelable(false);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ItemAlertDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement ItemAlertDialogListener");
        }
    }

    public interface ItemAlertDialogListener {
        void applyText(String item_title, double item_cost, int qty, String item_title_english);
    }
}
