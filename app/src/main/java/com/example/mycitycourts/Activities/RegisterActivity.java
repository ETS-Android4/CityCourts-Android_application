package com.example.mycitycourts.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycitycourts.Helpers.RegisterHelperClass;
import com.example.mycitycourts.Helpers.UserValidation;
import com.example.mycitycourts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/*
Add User To Realtime DB Firebase.
 */
public class RegisterActivity extends AppCompatActivity {
    EditText  et_UserName,et_Password,et_Email,et_BirthDate,et_CellPhone;
    CheckBox cb_terms;
    String str_password;
    String str_username;
    String str_email;
    String str_birthdate;
    String str_cellphone;
    UserValidation userValidation;
    FirebaseAuth auth;
    int maxid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getMeaning();
    }

    public void SignUp(View v){
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setFocusableInTouchMode(false);
        str_password = et_Password.getText().toString().trim();
        str_username = et_UserName.getText().toString().trim();
        str_email = et_Email.getText().toString().trim();
        str_birthdate = et_BirthDate.getText().toString().trim();
        str_cellphone = et_CellPhone.getText().toString().trim();
        if(userValidation.ClientSideCheck()) {
            if (!cb_terms.isChecked()) {
                cb_terms.setError("Must confirm to use City Court!");
                Toast.makeText(this,"Must confirm the terms to sign up",Toast.LENGTH_LONG).show();
            } else {
                cb_terms.setError(null);
                //userValidation.DBCheck();
                userValidation.UsernameInternalCheck();//he calls to Sign_up_fb_db with the right boolean !
            }
        }
    }
    public void TermsOfUse(View views){
        Intent intent = new Intent(getApplicationContext(),TermsActivity.class);
        startActivity(intent);
    }

    private void getMeaning() {
        auth= FirebaseAuth.getInstance();
        cb_terms = findViewById(R.id.cb_terms);
        et_UserName = findViewById(R.id.et_reg_username);
        et_Password = findViewById(R.id.et_reg_password);
        et_Email = findViewById(R.id.et_reg_email);
        et_BirthDate = findViewById(R.id.et_reg_birthdate);
        et_CellPhone = findViewById(R.id.et_reg_mob);
        userValidation = new UserValidation(et_UserName, et_Password, et_Email, et_BirthDate, et_CellPhone,this,this);
        maxid = 0;
        //id number take care.
    }
    public void Sign_up_fb_db(boolean Status){
        if(Status) {
            auth.createUserWithEmailAndPassword(str_email,str_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign Up Auth","Success !");
                        Toast.makeText(RegisterActivity.this, "Authentication Succeed.",Toast.LENGTH_LONG).show();
                        String uid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                        RegisterToInternalDB(uid);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign Up Auth", "Failure !", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Username already exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void RegisterToInternalDB(String uid){
        final RegisterHelperClass CurUser = new RegisterHelperClass(str_username, str_email, str_birthdate, str_cellphone);
        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(CurUser);
        finish();
    }

    public void CbChecked(View v){
        if (cb_terms.isChecked()){
            cb_terms.setError(null);
        }
    }


}