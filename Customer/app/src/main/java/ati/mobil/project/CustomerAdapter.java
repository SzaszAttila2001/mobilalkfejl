package ati.mobil.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> implements Filterable {

    private ArrayList<Customer> m_customer = new ArrayList<>();
    private ArrayList<Customer> m_customer_all = new ArrayList<>();
    private Context m_context;
    private int m_last_position = -1;


    public CustomerAdapter(Context context, ArrayList<Customer> customers) {
        this.m_customer = customers;
        this.m_customer_all = customers;
        this.m_context = context;
    }

    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(m_context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomerAdapter.ViewHolder holder, int position) {
        Customer akt_customer = m_customer.get(position);

        holder.bindTo(akt_customer);

        if (holder.getAdapterPosition() > m_last_position){
            Animation animation = AnimationUtils.loadAnimation(m_context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            m_last_position = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return m_customer.size();
    }

    @Override
    public Filter getFilter() {
        return customer_filter;
    }

    private Filter customer_filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Customer> filtered_customer = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0){
                results.count = m_customer_all.size();
                results.values = m_customer_all;
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Customer customer : m_customer_all) {
                    if(customer.getName().toLowerCase().contains(filterPattern)){
                        filtered_customer.add(customer);
                    }
                }

                results.count = filtered_customer.size();
                results.values = filtered_customer;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            m_customer = (ArrayList)results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
            private TextView m_name;
            private TextView m_status;
            private TextView m_reason;
            private TextView m_start;
            private TextView m_end;
            private TextView m_party;
            private TextView m_payment;

        public ViewHolder(View itemView) {
            super(itemView);
            this.m_name = itemView.findViewById(R.id.cusName);
            this.m_status = itemView.findViewById(R.id.cusStatus);
            this.m_reason = itemView.findViewById(R.id.cusStatusRe);
            this.m_start = itemView.findViewById(R.id.itemStart);
            this.m_end = itemView.findViewById(R.id.itemEnd);
            this.m_party = itemView.findViewById(R.id.itemParty);
            this.m_payment = itemView.findViewById(R.id.itemPayment);
        }

        public void bindTo(Customer current_customer){
            m_name.setText(current_customer.getName());
            m_status.setText(current_customer.getStatus());
            m_reason.setText(current_customer.getStatusReason());

            String dateStart = new SimpleDateFormat("yyyy-MM-dd").format(current_customer.getValidStart());
            String dateEnd = new SimpleDateFormat("yyyy-MM-dd").format(current_customer.getValidEnd());
            m_start.setText(dateStart);
            m_end.setText(dateEnd);

            m_party.setText(current_customer.getParty());
            m_payment.setText(current_customer.getPayment_method());

            itemView.findViewById(R.id.delete_customer).setOnClickListener(view -> ((CustomerListActivity)m_context).deleteCustomer(current_customer));
            itemView.findViewById(R.id.modify_customer).setOnClickListener(view -> ((CustomerListActivity)m_context).modifyCustomer(current_customer));
        }
    }


}
