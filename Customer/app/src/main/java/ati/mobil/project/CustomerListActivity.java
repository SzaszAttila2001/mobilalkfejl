package ati.mobil.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CustomerListActivity extends AppCompatActivity {

    private ActivityController m_controller;
    private static final String LOG_TAG = CustomerListActivity.class.getName();
    private static final String PREF_KEY = CustomerListActivity.class.getPackage().toString();
    private RecyclerView m_recView;
    private ArrayList<Customer> m_customers;
    private CustomerAdapter m_adapter;
    private FirebaseFirestore m_firestore;
    private CollectionReference m_collection;
    private int m_customer_limit = 10;
    private SharedPreferences m_preferences;
    private NotificationHandler m_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        m_controller = new ActivityController(this);

        m_recView = findViewById(R.id.customerListRecyclerView);
        m_recView.setLayoutManager(new GridLayoutManager(this, 1));

        m_customers = new ArrayList<>();
        m_adapter = new CustomerAdapter(this, m_customers);

        m_recView.setAdapter(m_adapter);

        m_firestore = FirebaseFirestore.getInstance();
        m_collection = m_firestore.collection("Customers");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(m_powerReciever, filter);

        m_preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        m_notification = new NotificationHandler(this);
    }

    BroadcastReceiver m_powerReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action){
                case Intent.ACTION_POWER_CONNECTED:
                    m_customer_limit = 10;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    m_customer_limit = 7;
                    break;
            }
            queryData();
        }
    };

    private void queryData(){
        m_customers.clear();

        m_collection.orderBy("name").limit(m_customer_limit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                Customer customer = doc.toObject(Customer.class);
                customer.setId(doc.getId());
                m_customers.add(customer);
            }

            if (m_customers.size() == 0){
                initData();
                queryData();
            }

            m_adapter.notifyDataSetChanged();
        });
    }

    public void deleteCustomer(Customer customer){
        DocumentReference documentReference = m_collection.document(customer._getId());

        documentReference.delete().addOnSuccessListener(succes -> {
            Toast.makeText(this, "Successfully deleted!", Toast.LENGTH_LONG).show();
            m_notification.send(customer.getName() + " successfully deleted!");
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this, "Cannot be deleted!", Toast.LENGTH_LONG).show();
        });
        queryData();
    }

    public void modifyCustomer(Customer customer){
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString("name", customer.getName());
        editor.putString("status", customer.getStatus());
        editor.putString("status_reason", customer.getStatusReason());

        String dateStart = new SimpleDateFormat("yyyy-MM-dd").format(customer.getValidStart());
        String dateEnd = new SimpleDateFormat("yyyy-MM-dd").format(customer.getValidEnd());
        editor.putString("start", dateStart);
        editor.putString("end", dateEnd);

        editor.putString("party", customer.getParty());
        editor.putString("payment", customer.getPayment_method());
        editor.putString("id", customer._getId());

        editor.apply();

        m_controller.to_activity(ActivityController.NEW_CUSTOMER_ACTIVITY);
    }


    private void initData()  {
        String[] customerNames = getResources().getStringArray(R.array.t_names);
        String[] customerStatus = getResources().getStringArray(R.array.t_status);
        String[] customerStatusReason = getResources().getStringArray(R.array.t_status_re);
        String[] customerStart = getResources().getStringArray(R.array.t_start);
        String[] customerEnd = getResources().getStringArray(R.array.t_end);
        String[] customerParty = getResources().getStringArray(R.array.t_party);
        String[] customerPayment = getResources().getStringArray(R.array.t_payment);

        for (int i = 0; i < customerNames.length; i++) {
            try {
                Date dateStart = new SimpleDateFormat("yyyy-MM-dd").parse(customerStart[i]);
                Date dateEnd = new SimpleDateFormat("yyyy-MM-dd").parse(customerEnd[i]);
                m_collection.add(new Customer(customerNames[i], customerStatus[i], customerStatusReason[i], dateStart, dateEnd, customerParty[i], customerPayment[i]));
                Log.i(LOG_TAG,"Customer party: " + customerParty[i]);
            }catch (Exception p){
                Log.e("CustomerListActivity", "Parse error during Date!");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                m_adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_out:
                Log.d(LOG_TAG, "Logout clicked!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_powerReciever);
    }
}