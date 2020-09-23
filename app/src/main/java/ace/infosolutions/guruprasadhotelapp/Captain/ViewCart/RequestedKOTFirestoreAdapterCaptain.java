package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import ace.infosolutions.guruprasadhotelapp.R;


public class RequestedKOTFirestoreAdapterCaptain extends FirestoreRecyclerAdapter<ViewCartPOJO, RequestedKOTFirestoreAdapterCaptain.CustomerHolder > {

    public RequestedKOTFirestoreAdapterCaptain(@NonNull FirestoreRecyclerOptions<ViewCartPOJO> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ViewCartPOJO model) {
        holder.item_title.setText(model.getItem_title());
        holder.item_qty.setText(""+model.getItem_qty());
        holder.item_cost.setText(""+model.getItem_cost());
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

