package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

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

import ace.infosolutions.guruprasadhotelapp.R;


public class ConfirmedCartCaptainAdapter extends FirestoreRecyclerAdapter<ViewCartModel, ConfirmedCartCaptainAdapter.CustomerHolder> {
    private TextView empty_cart;
    private ImageView empty_cartIV;

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public ConfirmedCartCaptainAdapter(@NonNull FirestoreRecyclerOptions<ViewCartModel> options, View view) {
        super(options);
        empty_cart = view.findViewById(R.id.empty_cart);
        empty_cartIV = view.findViewById(R.id.empty_cartIV);
    }

    public ConfirmedCartCaptainAdapter(@NonNull FirestoreRecyclerOptions<ViewCartModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ViewCartModel model) {
        holder.food_title.setText(model.getItem_title());
        holder.food_qty.setText("" + model.getItem_qty());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        try {
            if (getItemCount() == 0) {
                empty_cartIV.setVisibility(View.VISIBLE);
                empty_cart.setVisibility(View.VISIBLE);
            } else {
                empty_cartIV.setVisibility(View.GONE);
                empty_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_bill_items_captain, parent, false);
        return new CustomerHolder(view);
    }


    public class CustomerHolder extends RecyclerView.ViewHolder {
        private TextView food_title;
        private TextView food_qty;

        public CustomerHolder(@NonNull final View itemView) {
            super(itemView);
            food_title = itemView.findViewById(R.id.food_item_title);
            food_qty = itemView.findViewById(R.id.food_item_qty);

        }
    }


}

