package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.UpdateFoodMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ace.infosolutions.guruprasadhotelapp.Captain.Fish.FoodMenuModel;
import ace.infosolutions.guruprasadhotelapp.R;


public class UpdateFoodMenuFirestoreAdapter extends FirestoreRecyclerAdapter<FoodMenuModel, UpdateFoodMenuFirestoreAdapter.CustomerHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener listener2;

    public UpdateFoodMenuFirestoreAdapter(@NonNull FirestoreRecyclerOptions<FoodMenuModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull FoodMenuModel model) {
        holder.item_title.setText(model.getItem_title());
        holder.item_cost.setText("Rs." + model.getItem_cost());
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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

    public void setOnItemLongClickListener(OnItemLongClickListener listener2) {
        this.listener2 = listener2;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public interface OnItemLongClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
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
                        listener.onItemClick(getSnapshots().getSnapshot(pos));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if (pos != RecyclerView.NO_POSITION && listener2 != null) {
                        listener2.onItemClick(getSnapshots().getSnapshot(pos));
                    }
                    return false;
                }
            });

        }
    }
}

