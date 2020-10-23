package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

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

import ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList.ModelClasses.FinalBillModel;
import ace.infosolutions.guruprasadhotelapp.R;


public class ConfirmFinalBillFirestoreAdapter extends FirestoreRecyclerAdapter<FinalBillModel, ConfirmFinalBillFirestoreAdapter.CustomerHolder> {
    private View view;
    private ImageView empty_cartIV;
    private TextView empty_cartTV;
    private onFinalBillItemTitleClick listener_title;
    private onFinalBillItemCostClick listener_cost;
    private onFinalBillItemQtyClick listener_qty;
    private onFinalBillDeleteClick listener_delete;

    public ConfirmFinalBillFirestoreAdapter(@NonNull FirestoreRecyclerOptions<FinalBillModel> options) {
        super(options);
    }

    public ConfirmFinalBillFirestoreAdapter(@NonNull FirestoreRecyclerOptions<FinalBillModel> options, View view) {
        super(options);
        this.view = view;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull FinalBillModel model) {
        holder.item_title.setText(model.getItem_title());
        holder.item_qty.setText("" + model.getItem_qty());
        double rounded = (Math.round(model.getItem_cost() * 100.0) / 100.0);
        holder.item_cost.setText(String.valueOf(rounded));
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cart_itemfinal, parent, false);
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

    public void setOnFinalBillItemQtyClickListener(onFinalBillItemQtyClick listener) {
        this.listener_qty = listener;
    }

    public void setOnFinalBillItemCostClickListener(onFinalBillItemCostClick listener) {
        this.listener_cost = listener;
    }

    public void setOnFinalBillDeleteClickListener(onFinalBillDeleteClick listener) {
        this.listener_delete = listener;
    }

    public interface onFinalBillItemTitleClick {
        void onItemClick(DocumentSnapshot snapshot, int pos);
    }

    public interface onFinalBillItemQtyClick {
        void onItemClick(DocumentSnapshot snapshot, int pos);
    }

    public interface onFinalBillItemCostClick {
        void onItemClick(DocumentSnapshot snapshot, int pos);
    }

    public interface onFinalBillDeleteClick {
        void onItemClick(DocumentSnapshot snapshot, int pos);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        private TextView item_title;
        private TextView item_cost;
        private TextView item_qty;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.food_item_title);
            item_cost = itemView.findViewById(R.id.food_item_cost);
            item_qty = itemView.findViewById(R.id.food_item_qty);

            item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener_title != null) {
                        listener_title.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });

            item_qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener_qty != null) {
                        listener_qty.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });

            item_cost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener_cost != null) {
                        listener_cost.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener_delete != null) {
                        listener_delete.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                    return false;
                }
            });
        }
    }
}

