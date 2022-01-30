package com.example.mycitycourts.Activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener;
import com.example.mycitycourts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/*
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
/*
A Project Build By Eden Berdugo.
Todo: Delete from build.gradle :1.    implementation 'com.orhanobut:logger:2.2.0'
                                2. debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.5'
 */
public class MainActivity extends AppCompatActivity {
    private int count ;
    private CheckBox cbSave;
    private long startMillis;
    private EditText EmailET, PasswordEt;
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference usersDB;
    Button RegisterButton;
    PopupWindow popupWindow;
    View popupView;
    Button btn_Send,btn_Close;
// ...
// Initialize Firebase Auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.d("hello");
        Logger.wtf("what a Terrible Failure");
         */
        mAuth = FirebaseAuth.getInstance();
        count = 0;
        startMillis=0;
        EmailET = findViewById(R.id.tv_Email);
        PasswordEt = findViewById(R.id.tv_Password);
        rootNode = FirebaseDatabase.getInstance();
        usersDB = rootNode.getReference("users");
        RegisterButton = findViewById(R.id.btn_register);
        cbSave=findViewById(R.id.cb_SaveEmail);
        Instructor();
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        PasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    OnLogin(null);//match this behavior to your 'Send' (or Confirm) button
                }
                return true;
            }
        });
    }
    public void OnLogin(View view) {
        final String email = EmailET.getText().toString().trim();
        final String password = PasswordEt.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Must fill all the fields !", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            if (cbSave.isChecked()) {
                editor.putBoolean("CBsaveEmail", true);
                editor.putString("SavedEmail", email);
            } else {
                editor.putBoolean("CBsaveEmail", false);
                editor.putString("SavedEmail",null);
            }
            editor.apply();
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("users").child(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                                intent.putExtra("level",dataSnapshot.child("level").getValue(int.class));
                                intent.putExtra("Email",email);
                                intent.putExtra("username", dataSnapshot.child("username").getValue(String.class));
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Can't Sign in,Probably email/pass wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void OnRegister(View view){
        Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(i);
    }
    private void Instructor(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        if(pref.getBoolean("FirstTimeRegister", true)) {
            new BubbleShowCaseBuilder(this) //Activity instance
                    .title("Welcome To CityCourts !") //Any title for the bubble view
                    .description("To use CityCourts you must register first")
                    .targetView(RegisterButton).arrowPosition(BubbleShowCase.ArrowPosition.BOTTOM).listener(new BubbleShowCaseListener() {
                @Override
                public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
                    OnRegister(null);
                }

                @Override
                public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
                    bubbleShowCase.dismiss();
                }

                @Override
                public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                }

                @Override
                public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                    bubbleShowCase.dismiss();
                }
            }) //View to point out
                    .show();//Display the ShowCase
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("FirstTimeRegister",false);
            editor.apply();
        }
        else{
            if(pref.getBoolean("CBsaveEmail",false)){
                cbSave.setChecked(true);
                EmailET.setText(pref.getString("SavedEmail","Can't Find Saved Email"));
            }
        }
    }

//TODO This method will be deleted once i finish debugging, just use :BubbleShowCaseBuilder()... .showOnce(Unique id).
/*
On touch screen 5 times quickly to display instructions
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventaction = event.getAction();
        if (eventaction == MotionEvent.ACTION_UP) {

            //get system current milliseconds
            long time= System.currentTimeMillis();


            //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
            if (startMillis==0 || (time-startMillis> 1000) ) {
                startMillis=time;
                count=1;
            }
            //it is not the first, and it has been  less than 3 seconds since the first
            else{ //  time-startMillis< 3000
                count++;
            }

            if (count==5) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("FirstTimeRegister",true);
                editor.apply();
                Instructor();
            }
            return true;
        }
        return false;
    }


    public void onButtonShowPopupWindowClick(View view) {
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    public void onSendClicked(View v){
        EditText tv_email = popupView.findViewById(R.id.tv_emailInsert);
        if(tv_email.getText()==null||tv_email.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Email field left empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String email= tv_email.getText().toString().trim();
        mAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Email didn't send.\nMake sure there is signed account linked\nTo this mail.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    public void onCloseClicked(View v){
        popupWindow.dismiss();
    }

}
