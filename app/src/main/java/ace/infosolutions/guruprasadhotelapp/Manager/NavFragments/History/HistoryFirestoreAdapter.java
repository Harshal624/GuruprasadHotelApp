package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.History;

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

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.HistoryModel;
import ace.infosolutions.guruprasadhotelapp.R;


public class HistoryFirestoreAdapter extends FirestoreRecyclerAdapter<HistoryModel, HistoryFirestoreAdapter.CustomerHolder> {
    private View view;
    private ImageView empty_cartIV;
    private TextView empty_cartTV;
    private onFinalBillItemTitleClick listener_title;

    public HistoryFirestoreAdapter(@NonNull FirestoreRecyclerOptions<HistoryModel> options) {
        super(options);
    }

    public HistoryFirestoreAdapter(@NonNull FirestoreRecyclerOptions<HistoryModel> options, View view) {
        super(options);
        this.view = view;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull HistoryModel model) {
        holder.bill_no.setText(model.getBill_no());
        holder.date_time.setText(model.getDate_time_completed());
        holder.payment_mode.setText("(" + model.getPayment_mode() + ")");
        holder.table_no.setText("" + model.getTable_no());
        holder.table_type.setText(model.getTable_type() + ",");
        double roundedDouble = Math.round(model.getTotal_cost() * 100.0) / 100.0;
        holder.total_cost.setText("" + roundedDouble);

    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_history_item, parent, false);
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
        private TextView date_time;
        private TextView table_type;
        private TextView table_no;
        private TextView payment_mode;
        private TextView total_cost;


        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            bill_no = itemView.findViewById(R.id.bill_no);
            date_time = itemView.findViewById(R.id.date_time);
            table_type = itemView.findViewById(R.id.table_type);
            table_no = itemView.findViewById(R.id.table_no);
            total_cost = itemView.findViewById(R.id.total_cost);
            payment_mode = itemView.findViewById(R.id.payment_mode);

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

