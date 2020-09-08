package ace.infosolutions.guruprasadhotelapp.Captain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private ArrayList<String> itemtitle;
    private ArrayList<String> itemcost;
    OnItemClickListener2 onItemClickListener2;


    public ItemListAdapter(ArrayList<String> itemtitle, ArrayList<String> itemcost) {
        this.itemtitle = itemtitle;
        this.itemcost = itemcost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item_name.setText(itemtitle.get(position));
        holder.item_cost.setText("Rs."+itemcost.get(position));

    }

    @Override
    public int getItemCount() {
        return itemtitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView item_name;
        private TextView item_cost;
        private CardView food_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.food_item_title);
            item_cost = itemView.findViewById(R.id.food_item_cost);
            food_item = itemView.findViewById(R.id.food_item);

            food_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener2.onItemClick(item_name.getText().toString(),getAdapterPosition(),item_cost.getText().toString());
                }
            });

            food_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });


        }
    }

    public interface OnItemClickListener2{
        void onItemClick(String title,int position,String cost);
    }

    public void setOnItemClickListener2(OnItemClickListener2 onItemClickListener2){
        this.onItemClickListener2 = onItemClickListener2;
    }



}
