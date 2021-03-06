package com.dc.doctor_communication.DoctorMeeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dc.doctor_communication.DataManagement.Person1;
import com.dc.doctor_communication.ConditionAnalysis.Fragment_conditionAnalysis;
import com.dc.doctor_communication.DataManagement.Symptom2;
import com.dc.doctor_communication.MainActivity;
import com.dc.doctor_communication.R;
import com.dc.doctor_communication.Recording.Recording;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MeetingDoc extends AppCompatActivity {

    //?????? ????????? text
    private TextView notice_noData_text;
    private ImageView notice_noSelect;
    //???????????? - ?????? ????????? ????????? TextView
    private TextView startDateText;
    //???????????? - ?????? ????????? ????????? TextView
    private TextView endDateText;
    //???????????? ?????? ??? ?????? ?????????
    private TextView symptom_title;
    //?????? - ????????? ???????????? ???????????? ??????
    private TextView gotoGraph;
    //???????????? ?????? ??????
    private final int[] buttonKey = {R.id.btn_all_symptom,R.id.btn_1_symptom,R.id.btn_2_symptom,R.id.btn_3_symptom,R.id.btn_4_symptom,R.id.btn_5_symptom
            ,R.id.btn_6_symptom,R.id.btn_7_symptom,R.id.btn_8_symptom,R.id.btn_9_symptom,R.id.btn_10_symptom
            ,R.id.btn_11_symptom,R.id.btn_12_symptom,R.id.btn_13_symptom,R.id.btn_14_symptom,R.id.btn_15_symptom
            ,R.id.btn_16_symptom,R.id.btn_17_symptom,R.id.btn_18_symptom,R.id.btn_19_symptom,R.id.btn_20_symptom};
    //?????? - ???????????? ??????
    private final Button[] symptomBtn = new Button[buttonKey.length];
    //???????????? ?????? ??????
    private final String[] buttonValue = {"??????","??????", "??????", "??????","?????? ??????","??????","?????? ??????","??? ??????","????????? ??????","????????? ??????","??????","??????","?????????","??????","??? ??????","??????","??????","????????????","??????","????????????","?????? ??????"};
    //???????????? (????????????/?????????)
    private Calendar startDate, endDate;
    static FirebaseAuth firebaseAuth;
    int sizeList;
    //???????????? ?????????
    ExpandableListView expandableListView;
    CustomAdapter adapter;
    ArrayList<ParentData> groupListDatas;
    ArrayList<ArrayList<ContentData>> childListDatas;

    //????????? ????????? ???????????? ??? ?????? ?????? ?????????
    int btnClicked = -1;
    String fire_date;

    ConstraintLayout selectedDataLayout;
    ImageView notice_noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_doctor);
        Log.d("myapp", "???????????? ????????? ??????");

        //?????? ??????
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // ??? ????????????????????? ???????????? ?????? ?????? ??????
        //?????? ?????? - ????????????, ???????????????
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.dc_actionbar);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_btn);
        //?????? ?????? ??????
        for (int i = 0; i < buttonKey.length; i++) {
            symptomBtn[i] = findViewById(buttonKey[i]);
        }
        //datePicker??? ????????? ??????/????????? calendar ??????
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        //???????????? ????????? ??? ??????
        expandableListView = findViewById(R.id.DC_listview);



        // ????????? ?????? ????????? ???????????? ???????????? ??????
        gotoGraph = findViewById(R.id.btn_gotoGraph);
        gotoGraph.setOnClickListener(v -> {
            String graphDate = startDate.get(Calendar.YEAR)+""+startDate.get(Calendar.MONTH);
            //?????? ?????????
            GraphDialog graphDialog = new GraphDialog(MeetingDoc.this,graphDate,buttonValue[btnClicked]);
            graphDialog.setCancelable(true);
            graphDialog.setCanceledOnTouchOutside(true);
            graphDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            graphDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            graphDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            graphDialog.show();
        });

        //???????????? ?????? ??? ?????? ?????????
        symptom_title = findViewById(R.id.symptom_title);
        //?????? ????????? ?????? DatePicker??? ???????????? ??????(TextView)
        startDateText = findViewById(R.id.startDate);
        endDateText = findViewById(R.id.endDate);
        selectedDataLayout = findViewById(R.id.selectedDataLayout);
        notice_noData = findViewById(R.id.notice_noData);
        notice_noData_text = findViewById(R.id.notice_noData_text);
        notice_noSelect = findViewById(R.id.notice_noSelect);
// -> ?????? ?????? ??????
        //???????????? ????????? ??????
        setDateText(startDate);
        setDateText(endDate);
        //???????????? DatePickerDialog ??????
        startDateText.setOnClickListener(v -> {
            //DatePickerDialog ?????? ??????
            //R.style.MyDatePickerStyle,
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String PickedDate = year + "." + (month + 1) + "." + dayOfMonth;
                            startDateText.setText(PickedDate);
                            startDate.set(year,month+1,dayOfMonth);
                        }
                    }
                    //?????? ?????? ?????? ?????? (?????? ????????????)
                    , startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH)-1, startDate.get(Calendar.DAY_OF_MONTH)
            );
            //?????? ?????? ????????????
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            DatePicker datePicker = datePickerDialog.getDatePicker();
            datePicker.init(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH)-1,startDate.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener(){
                        @Override
                        public void onDateChanged(DatePicker view,int year, int month,int dayOfMonth){
                            if(btnClicked!=-1){
                                Log.d("myapp","????????? ?????????");
                                startDate.set(year,month+1,dayOfMonth);
                                setData(btnClicked);
                            }
                        }
                    });
            //DatePickerDialog ??????
            datePickerDialog.show();
        });
        //???????????? DatePickerDialog ??????
        endDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String PickedDate = year + "." + (month + 1) + "." + dayOfMonth;
                            endDateText.setText(PickedDate);
                            endDate.set(year,month+1,dayOfMonth);
                        }
                    }
                    , endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)-1, endDate.get(Calendar.DAY_OF_MONTH)

            );

            //?????? ?????? ????????????
            Calendar c = Calendar.getInstance();
            c.set(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH)-1,startDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            DatePicker datePicker = datePickerDialog.getDatePicker();
            datePicker.init(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)-1,endDate.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener(){
                @Override
                public void onDateChanged(DatePicker view,int year, int month,int dayOfMonth){

                    if(btnClicked!=-1){
                        Log.d("myapp","????????? ?????????");
                        endDate.set(year,month+1,dayOfMonth);
                        setData(btnClicked);
                    }
                }
            });
            //DatePickerDialog ??????
            datePickerDialog.show();
        });
    }



    //?????? ????????? ??????
    public void setDateText(Calendar cal){
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        startDateText.setText(mYear + "." + (mMonth + 1) + "." + mDay);
        endDateText.setText(mYear + "." + (mMonth + 1) + "." + mDay);
        cal.set(mYear,mMonth+1,mDay);
    }

    //?????? ?????? ?????? (??????????????? ????????? ??????)
    public boolean checkIsBetween(String date){
        Log.d("check_date", date);
        //?????? / ??? ??????
        int start = startDate.get(Calendar.YEAR)*10000+startDate.get(Calendar.MONTH)*100+startDate.get(Calendar.DAY_OF_MONTH);
        int end = endDate.get(Calendar.YEAR)*10000+endDate.get(Calendar.MONTH)*100+endDate.get(Calendar.DAY_OF_MONTH);
        Log.d("checkIsBetween","==============start : "+start+", end : "+end);
        if(Integer.parseInt(date)>=start && Integer.parseInt(date)<=end) return true;

        return false;
    }

    // -> toolbar ?????? (???????????? ??????)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    //????????? ????????? ??????
    private void setData(int valudIdx){
        Log.d("myapp","setData ?????????");
        //??????,?????? ????????? arraylist
        groupListDatas = new ArrayList<ParentData>();
        childListDatas = new ArrayList<ArrayList<ContentData>>();
        sizeList = 0;
        //?????? ????????? ??? (?????? ??????)
        String selectedSymptom = buttonValue[valudIdx];

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.KOREA);
        String today = sdf.format(date);

        //????????? ?????? ????????? ??????
        for(int i=1; i<=Integer.parseInt(EndDateOfMonth()); i++){
            fire_date = String.valueOf(i);
            if((int)(Math.log10(i)+1) == 1) fire_date = "0"+fire_date;
            fire_date = today +  fire_date;
            if(checkIsBetween(fire_date)){
                Log.d("myapp22","checkIsBetween?????????");

                if(selectedSymptom=="??????") {
                    gotoGraph.setVisibility(View.INVISIBLE);
                    for(int j=0; j<5; j++){
                        String finalStringDateValue = fire_date;
                        myRef.child(uid).child("date").child(finalStringDateValue).child(String.valueOf(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Symptom2 symptom = snapshot.getValue(Symptom2.class);

                                if((!symptom.getSymptom().equals("e"))) {
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

                                    String yoil = " ";
                                    try {
                                        Date date = sdf.parse(finalStringDateValue);
                                        calendar.setTime(date);
                                        yoil = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN);
                                    } catch (ParseException e) {
                                        Log.d("myapp","????????????");
                                        e.printStackTrace();
                                    }
                                    Log.d("parent", yoil+symptom.getPart()+","+symptom.getPainLevel());

                                    groupListDatas.add(new ParentData(
                                            finalStringDateValue + " ("+yoil+")",
                                            symptom.getPart(),
                                            symptom.getPainLevel())
                                    );

                                    childListDatas.add(new ArrayList<ContentData>());
                                    Log.d("additional",symptom.getAdditional()+"");
                                    childListDatas.get(sizeList).add(new ContentData(
                                            symptom.getPart() ,
                                            symptom.getPainLevel(),
                                            symptom.getPain_characteristics() ,
                                            symptom.getPain_situation(),
                                            symptom.getAccompany_pain(),
                                            symptom.getAdditional())
                                    );

                                    sizeList++;
                                    Log.d("myapp","sizeList : "+sizeList+"");
                                }
                                if(sizeList==0){
                                    selectedDataLayout.setVisibility(View.INVISIBLE);
                                    notice_noData.setVisibility(View.VISIBLE);
                                }else{
                                    selectedDataLayout.setVisibility(View.VISIBLE);
                                    notice_noData.setVisibility(View.INVISIBLE);
                                }
                                //Log.d("list", childListDatas.get(0).get(0).getPart());
                                adapter = new CustomAdapter(MeetingDoc.this,groupListDatas,childListDatas);
                                //????????? ??????
                                expandableListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                } else {
                    gotoGraph.setVisibility(View.VISIBLE);
                    for(int j=0; j<5; j++){
                        String finalStringDateValue = fire_date;
                        myRef.child(uid).child("date").child(finalStringDateValue).child(String.valueOf(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Symptom2 symptom = snapshot.getValue(Symptom2.class);

                                if((!symptom.getSymptom().equals("e")) && symptom.getSymptom().equals(selectedSymptom)) {
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

                                    String yoil = " ";
                                    try {
                                        Date date = sdf.parse(finalStringDateValue);
                                        calendar.setTime(date);
                                        yoil = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN);
                                    } catch (ParseException e) {
                                        Log.d("myapp","????????????");
                                        e.printStackTrace();
                                    }
                                    Log.d("parent", yoil+symptom.getPart()+","+symptom.getPainLevel());

                                    groupListDatas.add(new ParentData(
                                            finalStringDateValue + " ("+yoil+")",
                                            symptom.getPart(),
                                            symptom.getPainLevel())
                                    );

                                    childListDatas.add(new ArrayList<ContentData>());
                                    Log.d("additional",symptom.getAdditional()+"");
                                    childListDatas.get(sizeList).add(new ContentData(
                                            symptom.getPart() ,
                                            symptom.getPainLevel(),
                                            symptom.getPain_characteristics() ,
                                            symptom.getPain_situation(),
                                            symptom.getAccompany_pain(),
                                            symptom.getAdditional())
                                    );

                                    sizeList++;
                                    Log.d("myapp","sizeList : "+sizeList+"");
                                }
                                if(sizeList==0){
                                    selectedDataLayout.setVisibility(View.INVISIBLE);
                                    notice_noData.setVisibility(View.VISIBLE);
                                }else{
                                    selectedDataLayout.setVisibility(View.VISIBLE);
                                    notice_noData.setVisibility(View.INVISIBLE);
                                }
                                //Log.d("list", childListDatas.get(0).get(0).getPart());
                                adapter = new CustomAdapter(MeetingDoc.this,groupListDatas,childListDatas);
                                //????????? ??????
                                expandableListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
            }
        }

    }
    public static String EndDateOfMonth(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.KOREA);
        cal.set(year, month, end);
        String endDate = dateFormat.format(cal.getTime());
        return endDate;
    }
    //???????????? ????????? ????????????
    public void sympOnClick(View view){
        notice_noSelect.setVisibility(View.GONE);
        notice_noData_text.setVisibility(View.GONE);
        for(int i=0;i<buttonKey.length;i++){
            symptomBtn[i].setTextColor(Color.BLACK);
            symptomBtn[i].setBackgroundResource(R.drawable.dc_button_nonclicked);
            if(view.getId()==buttonKey[i]){
                symptomBtn[i].setTextColor(Color.WHITE);
                symptomBtn[i].setBackgroundResource(R.drawable.dc_button_clicked);
                Log.d("myapp", buttonValue[i] + " ?????? ??????");
                //????????? ??????
                symptom_title.setText(buttonValue[i]);
                //????????? ????????? ?????? ????????? ?????? (????????? ????????? ??????)
                setData(i);
                Log.d("myapp","????????? ???????????? ?????????");
                btnClicked = i;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.translate_none,R.anim.translate_none);
    }
}