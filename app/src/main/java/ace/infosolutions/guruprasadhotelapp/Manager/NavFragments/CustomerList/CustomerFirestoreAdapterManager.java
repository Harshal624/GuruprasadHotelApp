package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.CustomerList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.R;


public class CustomerFirestoreAdapterManager extends FirestoreRecyclerAdapter<customerclass, CustomerFirestoreAdapterManager.CustomerHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener listener1;
    private View view;
    private ImageView nocustIV;
    private TextView nocustTV;


    public CustomerFirestoreAdapterManager(@NonNull FirestoreRecyclerOptions<customerclass> options, View view) {
        super(options);
        this.view = view;
        nocustIV = view.findViewById(R.id.nocustIV);
        nocustTV = view.findViewById(R.id.nocustsTV);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull customerclass model) {
        holder.table_type.setText(model.getTable_type());
        double roundedDouble = Math.round(model.getConfirmed_cost() * 100.0) / 100.0;
        Context context = holder.itemView.getContext();
        if (model.isIsconfirmed()) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.tomatored));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
        holder.cost.setText("" + roundedDouble);
        holder.table_no.setText("" + model.getTable_no());

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        try {
            if (getItemCount() == 0) {
                nocustTV.setVisibility(View.VISIBLE);
                nocustIV.setVisibility(View.VISIBLE);
            } else {
                nocustTV.setVisibility(View.GONE);
                nocustIV.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlistitem2, parent, false);
        return new CustomerHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener1) {
        this.listener1 = listener1;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot, int pos);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        private TextView table_no;
        private TextView table_type;
        private TextView cost;
        private CardView cardView;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.tableno);
            cost = itemView.findViewById(R.id.cost);
            table_type = itemView.findViewById(R.id.tabletype);
            cardView = itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener1.onItemLongClick(getSnapshots().getSnapshot(pos), getAdapterPosition());
                    }
                    return false;
                }
            });
        }


    }
}

