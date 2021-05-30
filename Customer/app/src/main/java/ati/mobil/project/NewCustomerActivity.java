package ati.mobil.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public class NewCustomerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String LOG_TAG = NewCustomerActivity.class.getName();
    private static final String PREF_KEY = NewCustomerActivity.class.getPackage().toString();

    EditText m_name;
    EditText m_status_reason;
    private static EditText m_start;
    private static EditText m_end;
    Spinner m_party_spinner;
    Spinner m_status_spinner;
    Spinner m_payment_spinner;
    String m_status;
    String m_party;
    String m_payment;
    String id;
    TextView m_title;
    private Boolean m_modify = false;             //New customer or modify an existing
    Button m_regButton;

    private FirebaseFirestore m_firestore;
    private CollectionReference m_collection;

    private ArrayAdapter<CharSequence> m_party_adapter;
    private ArrayAdapter<CharSequence> m_status_adapter;
    private ArrayAdapter<CharSequence> m_payment_adapter;

    private ActivityController m_controller;

    private SharedPreferences m_preferences;

    private NotificationHandler m_notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        m_controller = new ActivityController(this);

        m_name = findViewById(R.id.newCustName);
        m_status_reason = findViewById(R.id.newCustStatusRe);

        m_start = findViewById(R.id.startText);
        m_end = findViewById(R.id.endText);

        m_party_spinner = findViewById(R.id.partySpinner);
        m_status_spinner = findViewById(R.id.statusSpinner);
        m_payment_spinner = findViewById(R.id.paymentSpinner);

        m_status_spinner.setOnItemSelectedListener(this);
        m_status_adapter = ArrayAdapter.createFromResource(this,
                R.array.status, android.R.layout.simple_spinner_item);
        m_status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_status_spinner.setAdapter(m_status_adapter);

        m_party_spinner.setOnItemSelectedListener(this);
        m_party_adapter = ArrayAdapter.createFromResource(this,
                R.array.engaged_party, android.R.layout.simple_spinner_item);
        m_party_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_party_spinner.setAdapter(m_party_adapter);

        m_payment_spinner.setOnItemSelectedListener(this);
        m_payment_adapter = ArrayAdapter.createFromResource(this,
                R.array.payment, android.R.layout.simple_spinner_item);
        m_payment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_payment_spinner.setAdapter(m_payment_adapter);


        m_firestore = FirebaseFirestore.getInstance();
        m_collection = m_firestore.collection("Customers");

        m_regButton = findViewById(R.id.regCustButton);

        m_title = findViewById(R.id.newCustTitle);

        m_notification = new NotificationHandler(this);

        initFromPref();
    }

    public void to_back(View view){
        finish();
    }

    public void register(View view) {
        try {
            String name = m_name.getText().toString();
            String re = m_status_reason.getText().toString();
            Date dateStart = new SimpleDateFormat("yyyy-MM-dd").parse(m_start.getText().toString());
            Date dateEnd = new SimpleDateFormat("yyyy-MM-dd").parse(m_end.getText().toString());
            Customer customer = new Customer(name, m_status, re, dateStart, dateEnd, m_party, m_payment);
            if (m_modify){
                m_collection.document(id).set(customer).addOnFailureListener(fail -> {
                    Toast.makeText(this, "Cannot be modified!", Toast.LENGTH_LONG).show();
                }).addOnSuccessListener(suc -> {
                    Toast.makeText(this, "Successfully modified!", Toast.LENGTH_LONG).show();
                    m_notification.send(name + " data's successfully modified!");
                });
            }else{
                m_collection.add(customer);
            }
            m_controller.to_activity(ActivityController.CUSTOMER_LIST_ACTIVITY);
        }catch (Exception e){
            Log.e(LOG_TAG, "Error during registration!");
            Toast.makeText(this, "Error during registration!", Toast.LENGTH_LONG).show();
        }
    }

    private void initFromPref(){
        m_preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String name = m_preferences.getString("name",null);
        if (name == null){
            return;
        }else {
            m_modify = true;
            m_regButton.setText("Módosít");
            m_title.setText(name + " módosítása");
        }
        String status = m_preferences.getString("status",null);
        String status_reason = m_preferences.getString("status_reason",null);
        String start = m_preferences.getString("start",null);
        String end = m_preferences.getString("end",null);
        String payment = m_preferences.getString("payment",null);
        String party = m_preferences.getString("party",null);
        id = m_preferences.getString("id",null);

        m_name.setText(name);
        m_status_reason.setText(status_reason);
        m_start.setText(start);
        m_end.setText(end);
        m_status_spinner.setSelection(getIndex(m_status_spinner, status));
        m_payment_spinner.setSelection(getIndex(m_payment_spinner, payment));
        m_party_spinner.setSelection(getIndex(m_party_spinner, party));


        SharedPreferences.Editor editor = m_preferences.edit();
        editor.clear();
        editor.apply();
    }

    private int getIndex(Spinner spinner, String str){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(str)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter().equals(m_party_adapter)){
            this.m_party = parent.getItemAtPosition(position).toString();
        }else if(parent.getAdapter().equals(m_payment_adapter)){
            this.m_payment = parent.getItemAtPosition(position).toString();
        }else{
            this.m_status = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent.getAdapter().equals(m_party_adapter)){
            this.m_party = parent.getItemAtPosition(1).toString();
        }else if(parent.getAdapter().equals(m_payment_adapter)){
            this.m_payment = parent.getItemAtPosition(1).toString();
        }else{
            this.m_status = parent.getItemAtPosition(1).toString();
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        Boolean start;

        public DatePickerFragment(boolean start){
            this.start = start;
        }

        @NonNull
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String temp_date = year + "-" + month + "-" + day;
            if (start) m_start.setText(temp_date);
            else m_end.setText(temp_date);
        }
    }

    public void showDatePicker(Boolean start) {
        DatePickerFragment newFragment = new DatePickerFragment(start);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void valid_start(View v){
        showDatePicker(true);
    }

    public void valid_end(View v){
        showDatePicker(false);
    }

}