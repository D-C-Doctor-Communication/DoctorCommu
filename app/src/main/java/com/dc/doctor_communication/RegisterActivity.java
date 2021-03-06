package com.dc.doctor_communication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dc.doctor_communication.DataManagement.Symptom;
import com.dc.doctor_communication.DataManagement.Symptom2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    EditText mEmailText, mPasswordText, mPasswordcheckText, mName;
    Button mregisterBtn;
    Button changeLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        firebaseAuth =  FirebaseAuth.getInstance();

        mName = (EditText)findViewById(R.id.nameEt);
        mEmailText = (EditText)findViewById(R.id.emailEt);
        mPasswordText = (EditText)findViewById(R.id.passwordEdt);
        mPasswordcheckText = (EditText)findViewById(R.id.passwordcheckEdt);
        mregisterBtn = (Button)findViewById(R.id.register2_btn);
        changeLogin = (Button) findViewById(R.id.register_t2);



        changeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_none,R.anim.translate_center_to_right);
            finish();
        });
        mregisterBtn.setOnClickListener(v -> {
            //?????? ?????? ????????????
            final String email = mEmailText.getText().toString().trim();
            String pwd = mPasswordText.getText().toString().trim();
            String pwdcheck = mPasswordcheckText.getText().toString().trim();
            String userName = mName.getText().toString().trim();

            if(email.isEmpty() && pwd.isEmpty()){
                Toast.makeText(getApplicationContext(), "???????????? ??????????????? ??????????????????!", Toast.LENGTH_SHORT).show();
            }else if(userName.isEmpty()){
                Toast.makeText(getApplicationContext(), "????????? ??????????????????!", Toast.LENGTH_SHORT).show();
            }else if(email.isEmpty()){
                Toast.makeText(getApplicationContext(), "???????????? ??????????????????!", Toast.LENGTH_SHORT).show();
            }else if(pwd.isEmpty()){
                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????!", Toast.LENGTH_SHORT).show();
            }else if(pwd.length()<6){
                Toast.makeText(getApplicationContext(), "??????????????? 6??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            } else {
                if (pwd.equals(pwdcheck)) {
                    Log.d(TAG, "?????? ?????? " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                    mDialog.setMessage("??????????????????...");
                    mDialog.show();

                    // ????????????????????? ???????????? ??????
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // ?????? ?????????
                            if (task.isSuccessful()) {
                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();

                                // ????????? ???????????? ?????????????????? ????????????????????? ??????
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("symptom", "e");
                                hashMap.put("part", "e");
                                hashMap.put("painLevel", "e");
                                hashMap.put("pain_characteristics", "e");
                                hashMap.put("pain_situation", "e");
                                hashMap.put("accompany_pain", "e");
                                hashMap.put("additional", "e");

                                hashMap.put("time", "e");
                                hashMap.put("scheduleName", "e");
                                hashMap.put("place", "e");
                                hashMap.put("clinic_type", "e");
                                hashMap.put("memo", "e");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference().child("users");

                                myRef.child(uid).child("email").setValue(email);
                                myRef.child(uid).child("name").setValue(name);

                                String date="";

                                for(int m=4; m<=9; m++){
                                    int lastDay = getLastDateOfMonth("2022" + m + "00");
                                    for(int i=1; i<=lastDay; i++) {
                                        int length = (int) (Math.log10(i) + 1);
                                        if (length == 1) {
                                            date = "20220" + m + "0" + i;
                                        } else {
                                            date = "20220" + m + i;
                                        }
                                        for(int j=0;j<5;j++){
                                            String jj = j+"";
                                            myRef.child(uid).child("date").child(date).child(jj).setValue(hashMap);
                                        }
                                    }
                                }

                                // ????????? ?????????????????? ?????? ????????? ????????????
                                Toast.makeText(RegisterActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                overridePendingTransition(R.anim.translate_none, R.anim.translate_center_to_right);
                                finish();

                            } else {
                                Log.e(TAG,email);
                                Log.e(TAG,pwd);
                                Log.e(TAG,pwdcheck);
                                Log.e(TAG,userName);
                                mDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "???????????? ????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                    });
                //???????????? ?????????
                } else {
                    Toast.makeText(RegisterActivity.this, "??????????????? ???????????? ????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    public int getLastDateOfMonth(String yyyyMMdd) {
        String year = yyyyMMdd.substring(0,4);
        String month = yyyyMMdd.substring(4,6);
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year),Integer.parseInt(month)-1,1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return lastDay;
    }


}
