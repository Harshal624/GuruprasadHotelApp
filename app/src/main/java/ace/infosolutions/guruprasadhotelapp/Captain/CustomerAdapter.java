package ace.infosolutions.guruprasadhotelapp.Captain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private ArrayList<customerclass> customerclass;

    public CustomerAdapter(ArrayList<customerclass> customerclasses) {
        this.customerclass = customerclasses;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlistitem,parent,false);
        CustomerViewHolder customerViewHolder = new CustomerViewHolder(view);
        return customerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        customerclass customerclass1 = customerclass.get(position);
        holder.table_no.setText(""+customerclass1.getTable_no());
        holder.cost.setText(""+customerclass1.getCost());
    }

    @Override
    public int getItemCount() {
        return customerclass.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{
        public TextView table_no;
        public TextView cost;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.tableno);
            cost = itemView.findViewById(R.id.cost);

        }
    }
}
