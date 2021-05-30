package ati.mobil.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    ActivityController m_controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_controller = new ActivityController(this);

    }

    public void to_new_costumer(View view) {
        m_controller.to_activity(ActivityController.NEW_CUSTOMER_ACTIVITY);
    }

    public void to_customer_list(View view){
        m_controller.to_activity(ActivityController.CUSTOMER_LIST_ACTIVITY);
    }

}