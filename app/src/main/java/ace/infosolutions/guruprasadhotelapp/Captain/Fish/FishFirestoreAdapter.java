package ace.infosolutions.guruprasadhotelapp.Captain.Fish;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.R;


public class FishFirestoreAdapter extends FirestoreRecyclerAdapter<FoodMenuModel, FishFirestoreAdapter.CustomerHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener listener1;

    public FishFirestoreAdapter(@NonNull FirestoreRecyclerOptions<FoodMenuModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull FoodMenuModel model) {
        holder.item_title.setText(model.getItem_title());
        holder.item_cost.setText("Rs." + model.getItem_cost());

    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_list, parent, false);
        return new CustomerHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener1) {
        this.listener1 = listener1;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        private TextView item_title;
        private TextView item_cost;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.food_item_title);
            item_cost = itemView.findViewById(R.id.food_item_cost);

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
                        listener1.onItemLongClick(getSnapshots().getSnapshot(pos));
                    }

                    return false;
                }
            });

        }
    }

}

