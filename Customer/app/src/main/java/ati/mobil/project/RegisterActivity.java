package ati.mobil.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    ActivityController m_controller;
    EditText m_email;
    EditText m_pass;

    FirebaseAuth m_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        m_controller = new ActivityController(this);
        m_email = findViewById(R.id.regEmail);
        m_pass = findViewById(R.id.regPass);
        m_auth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        String email = m_email.getText().toString();
        String pass = m_pass.getText().toString();

        m_auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    m_controller.to_activity(ActivityController.MAIN_ACTIVITY);
                }else{
                    Toast.makeText(RegisterActivity.this, "User was't created successfully: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}