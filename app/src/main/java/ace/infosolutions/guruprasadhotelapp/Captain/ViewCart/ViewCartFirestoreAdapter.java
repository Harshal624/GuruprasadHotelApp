package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.R;


public class ViewCartFirestoreAdapter extends FirestoreRecyclerAdapter<ViewCartPOJO, ViewCartFirestoreAdapter.CustomerHolder > {
    private OnItemClickListenerCart listener;
    private onQtyClickListener qtylistener;

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public ViewCartFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ViewCartPOJO> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ViewCartPOJO model) {
        holder.food_title.setText(model.getItem_title());
        holder.food_cost.setText("Rs."+model.getItem_cost());
        holder.food_qty.setText(""+model.getItem_qty());
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cart_item,parent,false);
        return new CustomerHolder(view);
    }


    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView food_title;
        private TextView food_cost;
        private TextView food_qty;
        private ImageButton delete_order;

        public CustomerHolder(@NonNull final View itemView) {
            super(itemView);
            food_title = itemView.findViewById(R.id.food_item_title);
            food_cost = itemView.findViewById(R.id.food_item_cost);
            food_qty = itemView.findViewById(R.id.food_item_qty);
            delete_order = itemView.findViewById(R.id.delete_order);


            delete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if(pos != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClickCart(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });

            food_qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* String title = getSnapshots().getSnapshot(getAdapterPosition()).getString("item_title");
                    Toast.makeText(itemView.getContext(), title, Toast.LENGTH_SHORT).show();*/
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if(pos != RecyclerView.NO_POSITION && listener!=null)
                        qtylistener.onQtyClick(getSnapshots().getSnapshot(pos));
                }
            });
        }
    }

    public interface OnItemClickListenerCart{
        void onItemClickCart(DocumentSnapshot documentSnapshot, int position);
    }

   public void setOnItemCartClickListener(OnItemClickListenerCart listener) {
        this.listener = listener;
   }

   public interface onQtyClickListener{
        void onQtyClick(DocumentSnapshot snapshot);
   }
   public void setOnQtyClickListener(onQtyClickListener qtylistener){
        this.qtylistener = qtylistener;
   }

}

