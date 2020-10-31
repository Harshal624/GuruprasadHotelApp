package ace.infosolutions.guruprasadhotelapp.Manager.NavFragments.TallyExcel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ace.infosolutions.guruprasadhotelapp.R;

public class DailyOrderAdapter extends RecyclerView.Adapter<DailyOrderAdapter.ViewHolder> {
    private ArrayList<DailyOrderTallyPOJO> arrayList;

    public DailyOrderAdapter(ArrayList<DailyOrderTallyPOJO> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyordertally_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.time.setText(arrayList.get(position).getTime_completed());
        holder.table_no.setText("" + arrayList.get(position).getTable_no());
        holder.table_type.setText(arrayList.get(position).getTable_type());
        holder.payment_mode.setText(arrayList.get(position).getPayment_mode());
        holder.bill_no.setText(arrayList.get(position).getBILL_NO());
        holder.subtotal.setText("" + arrayList.get(position).getSubtotal());
        holder.discount.setText("" + arrayList.get(position).getDiscount());
        holder.total_cost.setText("" + arrayList.get(position).getTotal_cost());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time, table_no, table_type, payment_mode,
                bill_no, subtotal, discount, total_cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            table_no = itemView.findViewById(R.id.table_no);
            table_type = itemView.findViewById(R.id.table_type);
            payment_mode = itemView.findViewById(R.id.payment_mode);
            bill_no = itemView.findViewById(R.id.bill_no);
            subtotal = itemView.findViewById(R.id.subtotal);
            discount = itemView.findViewById(R.id.discount);
            total_cost = itemView.findViewById(R.id.total_cost);
        }
    }
}
