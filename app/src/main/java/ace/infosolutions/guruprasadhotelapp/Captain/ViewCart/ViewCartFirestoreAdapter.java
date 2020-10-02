package ace.infosolutions.guruprasadhotelapp.Captain.ViewCart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.R;


public class ViewCartFirestoreAdapter extends FirestoreRecyclerAdapter<ViewCartModel, ViewCartFirestoreAdapter.CustomerHolder > {
    private OnItemClickListenerCart listener;
    private onQtyClickListener qtylistener;
    private TextView empty_cart;
    private ImageView empty_cartIV;

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public ViewCartFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ViewCartModel> options, View view) {
        super(options);
        empty_cart = view.findViewById(R.id.empty_cart);
        empty_cartIV = view.findViewById(R.id.empty_cartIV);
    }
    public ViewCartFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ViewCartModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull ViewCartModel model) {
        holder.food_title.setText(model.getItem_title());
        holder.food_qty.setText(""+model.getItem_qty());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        try {
            if(getItemCount() == 0){
                empty_cartIV.setVisibility(View.VISIBLE);
                empty_cart.setVisibility(View.VISIBLE);
            }
            else{
                empty_cartIV.setVisibility(View.GONE);
                empty_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Exception",e.toString());
        }
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cart_item,parent,false);
        return new CustomerHolder(view);
    }


    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView food_title;
        private TextView food_qty;
        private ImageButton delete_order;

        public CustomerHolder(@NonNull final View itemView) {
            super(itemView);
            food_title = itemView.findViewById(R.id.food_item_title);
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

