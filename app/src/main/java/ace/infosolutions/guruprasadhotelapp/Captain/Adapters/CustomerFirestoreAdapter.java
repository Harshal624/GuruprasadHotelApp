package ace.infosolutions.guruprasadhotelapp.Captain.Adapters;

import android.media.Image;
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

import ace.infosolutions.guruprasadhotelapp.Captain.ModelClasses.customerclass;
import ace.infosolutions.guruprasadhotelapp.R;


public class CustomerFirestoreAdapter extends FirestoreRecyclerAdapter<customerclass, CustomerFirestoreAdapter.CustomerHolder > {
    private OnItemClickListener listener;
    private OnItemLongClickListener listener1;
    private View view;
    private ImageView nocustIV;
    private TextView nocustTV;


    public CustomerFirestoreAdapter(@NonNull FirestoreRecyclerOptions<customerclass> options,View view) {
        super(options);
        this.view = view;
        nocustIV = (ImageView)view.findViewById(R.id.nocustIV);
        nocustTV = (TextView)view.findViewById(R.id.nocustsTV);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerHolder holder, int position, @NonNull customerclass model) {
        holder.table_type.setText(model.getTable_type());
        holder.cost.setText(""+model.getConfirmed_cost());
        holder.table_no.setText(""+model.getTable_no());

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if(getItemCount() == 0){
            nocustTV.setVisibility(View.VISIBLE);
            nocustIV.setVisibility(View.VISIBLE);
        }
        else{
            nocustTV.setVisibility(View.GONE);
            nocustIV.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerlistitem,parent,false);
        return new CustomerHolder(view);
    }


    public class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView table_no;
        private TextView table_type;
        private TextView cost;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            table_no = itemView.findViewById(R.id.tableno);
            cost = itemView.findViewById(R.id.cost);
            table_type = itemView.findViewById(R.id.tabletype);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if(pos != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    //if item is removed and it's in his remove animation
                    if(pos != RecyclerView.NO_POSITION && listener!=null){
                        listener1.onItemLongClick(getSnapshots().getSnapshot(pos),getAdapterPosition());
                    }
                    return false;
                }
            });
        }


    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot documentSnapshot,int pos);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener1){
        this.listener1 = listener1;
    }
}

