package ace.infosolutions.guruprasadhotelapp.Captain.Parcel;

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

import ace.infosolutions.guruprasadhotelapp.R;


public class ParcelFirestoreAdapter extends FirestoreRecyclerAdapter<ParcelModel, ParcelFirestoreAdapter.CustomerHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener listener1;
    private View view;
    private ImageView nocustIV;
    private TextView nocustTV;


    public ParcelFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ParcelModel> options, View view) {
        super(options);
        this.view = view;
        nocustIV = (ImageView) view.findViewById(R.id.nocustIV);
        nocustTV = (TextView) view.findViewById(R.id.nocustsTV);
    }

    public ParcelFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ParcelModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ParcelModel model) {
        if (model.isIshomedelivery() == false) {
            holder.parcel_type.setText("Parcel");
        } else {
            holder.parcel_type.setText("Home Delivery");
        }
        double roundedDouble = Math.round(model.getConfirmed_cost() * 100.0) / 100.0;
        holder.total_cost.setText("" + roundedDouble);
        holder.cust_name.setText("" + model.getCustomer_name());

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parcel_list_item, parent, false);
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
        private TextView cust_name;
        private TextView total_cost;
        private TextView parcel_type;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            cust_name = itemView.findViewById(R.id.cust_name);
            total_cost = itemView.findViewById(R.id.parcel_cost);
            parcel_type = itemView.findViewById(R.id.parcel_type);

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

