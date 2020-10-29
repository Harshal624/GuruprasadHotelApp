package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.ParcelHistory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.Captain.Parcel.ParcelHistoryModel;
import ace.infosolutions.guruprasadhotelapp.R;


public class ParcelHistoryFirestoreAdapter extends FirestoreRecyclerAdapter<ParcelHistoryModel, ParcelHistoryFirestoreAdapter.CustomerHolder> {
    private View view;
    private ImageView empty_cartIV;
    private TextView empty_cartTV;
    private onFinalBillItemTitleClick listener_title;

    public ParcelHistoryFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ParcelHistoryModel> options) {
        super(options);
    }

    public ParcelHistoryFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ParcelHistoryModel> options, View view) {
        super(options);
        this.view = view;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ParcelHistoryModel model) {
        holder.payment_mode.setText("(" + model.getPayment_mode() + ")");
        double totalcost = Math.round(model.getTotal_cost() * 100.0) / 100.0;
        holder.total_cost.setText("" + totalcost);
        holder.date_time_arrived.setText(model.getDate_arrived() + " " + model.getTime_arrived());
        holder.date_time_completed.setText(model.getDate_completed() + " " + model.getTime_completed());
        if (model.isIshomedelivery()) {
            holder.delivery_type.setText("Home Delivery");
        } else {
            holder.delivery_type.setText("Parcel");
        }
        holder.cust_name.setText(model.getCustomer_name());
        holder.bill_no.setText(model.getBill_no());
        double totaldiscount = Math.round(model.getDiscount() * 100.0) / 100.0;
        holder.discount.setText(String.valueOf(totaldiscount));
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parcel_history_item, parent, false);
        return new CustomerHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        try {
            empty_cartIV = view.findViewById(R.id.empty_cartIV);
            empty_cartTV = view.findViewById(R.id.empty_cart);
            if (getItemCount() == 0) {
                empty_cartIV.setVisibility(View.VISIBLE);
                empty_cartTV.setVisibility(View.VISIBLE);
            } else {
                empty_cartIV.setVisibility(View.GONE);
                empty_cartTV.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    public void setOnFinalBillItemTitleClickListener(onFinalBillItemTitleClick listener) {
        this.listener_title = listener;
    }

    public interface onFinalBillItemTitleClick {
        void onItemClick(DocumentSnapshot snapshot, int pos);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        private TextView bill_no;
        private TextView cust_name;
        private TextView delivery_type;
        private TextView date_time_arrived;
        private TextView date_time_completed;
        private TextView total_cost;
        private TextView payment_mode;
        private TextView discount;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            bill_no = itemView.findViewById(R.id.bill_no);
            cust_name = itemView.findViewById(R.id.cust_name);
            delivery_type = itemView.findViewById(R.id.delivery);
            date_time_arrived = itemView.findViewById(R.id.date_time_arrived);
            date_time_completed = itemView.findViewById(R.id.date_time_completed);
            total_cost = itemView.findViewById(R.id.total_cost);
            payment_mode = itemView.findViewById(R.id.payment_mode);
            discount = itemView.findViewById(R.id.discount_total);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if (pos != RecyclerView.NO_POSITION && listener_title != null) {
                        listener_title.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });
        }
    }
}

