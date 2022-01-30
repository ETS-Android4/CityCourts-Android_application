package com.example.mycitycourts.Helpers;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mycitycourts.R;
import com.example.mycitycourts.Activities.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidation implements View.OnFocusChangeListener {
    DatabaseReference usersDB;
    boolean birthdate,username,phoneNumber,email,password;
    EditText  et_UserName,et_Password,et_Email,et_BirthDate,et_CellPhone;
    boolean db_user,db_phone,db_email;
    private Context previousContext;
    private ArrayList<EditText> et_List;
    private Calendar myCalendar;

    private RegisterActivity Owner;
    public UserValidation(EditText et_UserName, EditText et_Password, EditText et_Email, EditText et_BirthDate, EditText et_CellPhone,Context context,RegisterActivity registerActivity) {
        Owner=registerActivity;
        previousContext=context;
        db_user=false;
        db_phone=false;
        db_email=false;
        birthdate=false;
        username=false;
        phoneNumber=false;
        password=false;
        email=false;
        this.et_UserName = et_UserName;
        this.et_Password = et_Password;
        this.et_Email = et_Email;
        this.et_BirthDate = et_BirthDate;
        this.et_CellPhone = et_CellPhone;
        AddListeners();
    }

    public boolean isBirthdate() {
        return birthdate;
    }

    public void setBirthdate(boolean birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isUsername() {
        return username;
    }

    public void setUsername(boolean username) {
        this.username = username;
    }

    public boolean isPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(boolean phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.et_reg_username:
                    String input=et_UserName.getText().toString().trim();
                if(input.length()<3){
                        et_UserName.setError("must Be at least 3 chars");
                        username=false;
                    }
                else if(!UsernameLegal()){
                    et_UserName.setError("Can't contain .#$[] ");
                    username=false;
                }
                else if(input.contains(" ")){
                    et_UserName.setError("Can't contain white space ' '");
                    username=false;
                }
                else
                    {
                        et_UserName.setError(null);
                        username=true;
                    }
                break;
                case R.id.et_reg_password:
                    if (et_Password.getText().toString().trim().length() < 8) {
                        password = false;
                        et_Password.setError("Must contain at least 8 chars");
                    }
                    else if(et_Password.getText().toString().trim().contains(" ")){
                        et_UserName.setError("Can't contain white space ' '");
                        username=false;
                    }
                    else {
                        et_Password.setError(null);
                        password = true;
                    }
                    break;
                case R.id.et_reg_email:
                    if (!validateMail(et_Email.getText().toString().trim()))
                    {
                        et_Email.setError("Email illegal");
                        email=false;
                    }
                    else if(et_Email.getText().toString().trim().contains(" ")){
                        et_UserName.setError("Can't contain white space ' '");
                        username=false;
                    }
                    else
                    {
                        email=true;
                        et_Email.setError(null);
                    }
                    break;
                case R.id.et_reg_mob:
                    if (!onlyNumbers(et_CellPhone.getText().toString().trim())) {//if only numbers return false - one of values must be numbers
                        et_CellPhone.setError("Only numbers.");
                        phoneNumber = false;
                    }
                    else if (et_CellPhone.getText().toString().trim().length() != 10)
                    {//if only numbers return false - one of values must be numbers
                        et_CellPhone.setError("Number not legal");
                        phoneNumber = false;
                    }
                    else if(et_CellPhone.getText().toString().trim().contains(" ")){
                        et_UserName.setError("Can't contain white space ' '");
                        username=false;
                    }
                    else
                    {
                        et_CellPhone.setError(null);
                        phoneNumber = true;
                    }
                    break;
            }
        }

//        else if(v.getId()==R.id.et_reg_birthdate){
//            new DatePickerDialog(previousContext, date, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        }
    }

    private void AddListeners() {
        et_List = new ArrayList<>();
        et_List.add(et_BirthDate);
        et_List.add(et_CellPhone);
        et_List.add(et_UserName);
        et_List.add(et_Email);
        et_List.add(et_Password);
        usersDB = FirebaseDatabase.getInstance().getReference("users");
        et_UserName.setOnFocusChangeListener(this);
        et_Password.setOnFocusChangeListener(this);
        et_Email.setOnFocusChangeListener(this);
        et_CellPhone.setOnFocusChangeListener(this);
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        et_BirthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(previousContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_BirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new DatePickerDialog(previousContext, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }
        private boolean onlyNumbers(String ... details) {
            String regex = "[0-9]+";
            Pattern p = Pattern.compile(regex);
            for (String str:details) {
                if (str == null) return false;
                Matcher m = p.matcher(str);
                if(!m.matches())return false;
            }
            return true;
        }
    private boolean validateMail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public boolean ClientSideCheck(){
        if(username&&birthdate&&phoneNumber&&password&&email)return true;
        else {
            boolean et_left_empty=false;
            for(EditText et: et_List){
                if(et.getText().toString().trim().isEmpty()){
                    et.setError("Must be filled.");
                    et_left_empty=true;
                }
            }
            if(et_left_empty) Toast.makeText(previousContext, "One Or More of the fields left empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void updateLabel(){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_BirthDate.setText(sdf.format(myCalendar.getTime()));
        birthdate=true;
        et_CellPhone.requestFocus();
    }

    private boolean UsernameLegal(){
        String field_name=et_UserName.getText().toString().trim();
        return !field_name.contains("#") && !field_name.contains(".") && !field_name.contains("$") && !field_name.contains("]") && !field_name.contains("[");
    }


    public void UsernameInternalCheck(){
        usersDB.orderByChild("username").equalTo(et_UserName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner.Sign_up_fb_db(!snapshot.exists());//Will Send True if he doesnt exist !!!
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

