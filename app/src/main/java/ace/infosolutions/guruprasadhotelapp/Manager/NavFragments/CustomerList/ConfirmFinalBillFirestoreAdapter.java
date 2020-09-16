package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.RequestedKOT.requestedkotmodel;
import ace.infosolutions.guruprasadhotelapp.R;


public class ConfirmFinalBillFirestoreAdapter extends FirestoreRecyclerAdapter<FinalBillClass, ConfirmFinalBillFirestoreAdapter.CustomerHolder > {

    public ConfirmFinalBillFirestoreAdapter(@NonNull FirestoreRecyclerOptions<FinalBillClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull FinalBillClass model) {
        holder.item_title.setText(model.getItem_title());
        holder.item_qty.setText(""+model.getItem_qty());
        holder.item_cost.setText("Rs. "+model.getItem_cost());
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_bill_items,parent,false);
        return new CustomerHolder(view);
    }


    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView item_title;
        private TextView item_cost;
        private TextView item_qty;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.final_billTitle);
            item_cost = itemView.findViewById(R.id.final_billCost);
            item_qty = itemView.findViewById(R.id.final_billQty);
        }
    }




}

