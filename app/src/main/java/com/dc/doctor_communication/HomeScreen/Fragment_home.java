package com.dc.doctor_communication.HomeScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dc.doctor_communication.ConditionAnalysis.Fragment_conditionAnalysis;
import com.dc.doctor_communication.DataManagement.Person1;
import com.dc.doctor_communication.DataManagement.Symptom2;
import com.dc.doctor_communication.DoctorMeeting.MeetingDoc;
import com.dc.doctor_communication.Emergency.EmergencySearchList;
import com.dc.doctor_communication.HomeScreen.HomeListViewAdapter;
import com.dc.doctor_communication.MainActivity;
import com.dc.doctor_communication.R;
import com.dc.doctor_communication.Settings.SettingActivity;
import com.dc.doctor_communication.SymptomRegistration.Search;
import com.dc.doctor_communication.SymptomRegistration.SearchList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;


public class Fragment_home extends Fragment {
    int count = -1;

    static FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference myRef = database.getReference().child("users");
    static FirebaseUser user = firebaseAuth.getCurrentUser();
    static String uid = user.getUid();

    //???????????? ?????? ????????? ?????????
    static String get_symptom="";
    static boolean isDataExist = false;
    static String fire_date="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d("myapp","home??? ??????");
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get_name = snapshot.child("name").getValue(String.class);
                TextView helloUser = view.findViewById(R.id.user_name);
                helloUser.setText(get_name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        //????????? ?????? ????????????


//??????


        //?????? - ???????????? ??????
        Button btn_addSymptom = (Button)view.findViewById(R.id.btn_addSymptom);
        //?????? - ???????????? ?????? ??????
        Button btn_meetingDoc = (Button)view.findViewById(R.id.btn_meetingDoc);
        //?????? - ???????????? ??????
        Button btn_recording = (Button)view.findViewById(R.id.btn_recording);
        //??????????????? - ??? ????????? ?????? ?????????
        CardView[] wCalender = new CardView[7];
        wCalender[0] = view.findViewById(R.id.wCalender_sun); //?????????
        wCalender[1] = view.findViewById(R.id.wCalender_mon); //?????????
        wCalender[2] = view.findViewById(R.id.wCalender_tue); //?????????
        wCalender[3] = view.findViewById(R.id.wCalender_wed); //?????????
        wCalender[4] = view.findViewById(R.id.wCalender_thu); //?????????
        wCalender[5] = view.findViewById(R.id.wCalender_fri); //?????????
        wCalender[6] = view.findViewById(R.id.wCalender_sat); //?????????

//??????1 - ?????????????????? ??????
        btn_addSymptom.setOnClickListener(v -> { //???????????? ?????? ~ new Button.OnClickListener()??? ?????? ??????
            Intent addSymptom = new Intent(getContext(), SearchList.class);
            addSymptom.putExtra("count",count);
            startActivity(addSymptom);
            getActivity().overridePendingTransition(R.anim.translate_none,R.anim.translate_none);
        });

//??????2 - ???????????? ???????????? ??????
        btn_meetingDoc.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MeetingDoc.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.translate_none,R.anim.translate_none);
        });

//??????3 - ?????? ??????
        btn_recording.setOnClickListener(v -> {
            Intent addSymptom = new Intent(getContext(), EmergencySearchList.class);
            addSymptom.putExtra("count",count);
            startActivity(addSymptom);
            getActivity().overridePendingTransition(R.anim.translate_none,R.anim.translate_none);
        });




//?????? ????????? - ??? ????????? ????????? ????????? ????????? ??????

        TextView ymTextView = view.findViewById(R.id.ymTextView); //0000??? 00??? ????????????
        TextView[] wDate = new TextView[7];
        wDate[0] = view.findViewById(R.id.SUN_num); //??? ~ ??? ???????????? ????????????
        wDate[1] = view.findViewById(R.id.MON_num);
        wDate[2] = view.findViewById(R.id.TUE_num);
        wDate[3] = view.findViewById(R.id.WED_num);
        wDate[4] = view.findViewById(R.id.THU_num);
        wDate[5] = view.findViewById(R.id.FRI_num);
        wDate[6] = view.findViewById(R.id.SAT_num);

        WeekCalendar weekCalendar = new WeekCalendar();
        Date todayDate = new Date();
        //?????????
        weekCalendar.setWeekCalenderDate(view,todayDate,ymTextView,wDate);
        //???????????? ???????????? (????????? ?????? ????????????)
        WeekCalendar.setCardColor(todayDate.getDay(),wCalender);

//ListView
        ListView listView = (ListView)view.findViewById(R.id.home_listView);
        //????????? ?????? ????????? ?????????
        WeekCalendar.createDataListToday(ymTextView,wDate,listView);

        //??? ????????? ???????????? ??? ????????? ???????????? ????????? ????????????
        wCalender[0].setOnClickListener(v -> { //?????????
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,0,listView);
            WeekCalendar.setCardColor(0,wCalender);
        });
        wCalender[1].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,1,listView);
            WeekCalendar.setCardColor(1,wCalender);
        });
        wCalender[2].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,2,listView);
            wCalender[2].setCardBackgroundColor(Color.parseColor("#0078ff"));
            WeekCalendar.setCardColor(2,wCalender);
        });
        wCalender[3].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,3,listView);
            wCalender[3].setCardBackgroundColor(Color.parseColor("#0078ff"));
            WeekCalendar.setCardColor(3,wCalender);
        });
        wCalender[4].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,4,listView);
            wCalender[4].setCardBackgroundColor(Color.parseColor("#0078ff"));
            WeekCalendar.setCardColor(4,wCalender);
        });
        wCalender[5].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,5,listView);
            wCalender[5].setCardBackgroundColor(Color.parseColor("#0078ff"));
            WeekCalendar.setCardColor(5,wCalender);
        });
        wCalender[6].setOnClickListener(v -> {
            Log.d("myapp","????????? ??????");
            WeekCalendar.createDataList(ymTextView,wDate,6,listView);
            wCalender[6].setCardBackgroundColor(Color.parseColor("#0078ff"));
            WeekCalendar.setCardColor(6,wCalender);
        });

        return view;
    }



    static class WeekCalendar{
        static void createDataListToday(TextView ymTextView, TextView[] wDate, ListView listView){
            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            createDataList(ymTextView,wDate,calendar.get(Calendar.DAY_OF_WEEK)-1,listView);
        }
        static void setCardColor(int index, CardView[] wCalender){
            wCalender[index].setCardBackgroundColor(Color.parseColor("#A1C5EE"));
            for(int i=0;i<7;i++){
                if(i==index) continue;
                wCalender[i].setCardBackgroundColor(Color.parseColor("#0a000000"));
            }
        }
        static void createDataList(TextView ymTextView, TextView[] wDate, int index, ListView listView){
            //0000.00.00????????? String ?????????
            String clickedDate = ymTextView.getText().toString().substring(0,4)+""+ymTextView.getText().toString().substring(6,8)+""+wDate[index].getText().toString();

            //listView ?????? ??? Adapter ??????
            HomeListViewAdapter adapter2 = new HomeListViewAdapter();
            //Adapter ??????
            listView.setAdapter(adapter2);



            for(int i=0; i<5; i++){
                Log.d("fire_j", String.valueOf(i));
                myRef.child(uid).child("date").child(clickedDate).child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String get_symptom = snapshot.child("symptom").getValue(String.class);
                        String get_part = snapshot.child("part").getValue(String.class);
                        String get_painLevel = snapshot.child("painLevel").getValue(String.class);
                        String get_characteristics = snapshot.child("pain_characteristics").getValue(String.class);
                        String get_situation = snapshot.child("pain_situation").getValue(String.class);

                        Log.d("get_fire", get_symptom+","+get_part+","+get_painLevel+","+get_characteristics+","+get_situation);
                        if(!get_part.equals("e")) {
                            Log.d("fire_in", get_symptom);
                            switch (get_symptom){
                                case "??????":
                                    Log.d("ee",get_symptom);
                                    //   adapter2.addItem(Person1.symptom[0].getPart(),R.drawable.img_pain_sym1,Integer.parseInt(Person1.symptom[0].getPain_level()),Person1.symptom[0].getPain_characteristics(),Person1.symptom[0].getPain_situation());

                                    adapter2.addItem(get_symptom, R.drawable.symptom_head_preview, Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "??????": case "?????????": case "??????": case "??? ??????": case "??????": case "??????": case "??????": case "??????": case "??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_face_preview, Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "????????? ??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_arm_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "?????? ??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_leg_preview, Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_back_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_chest_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "??????": case "??? ??????": case "????????????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_stomach_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "????????? ??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_buttock_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "??????": case "??????": case "????????????": case "??????": case "?????? ??????": case "??????": case "?????????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_body_preview, Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "?????? ??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_hand_preview , Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                case "?????? ??????":
                                    adapter2.addItem(get_symptom, R.drawable.symptom_foot_preview, Integer.parseInt(get_painLevel),get_characteristics, get_situation);
                                    break;
                                default:
                            }
                            adapter2.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }

            adapter2.notifyDataSetChanged();
            Log.d("myapp","Adapter added");
        }

        @SuppressLint("SetTextI18n")
        void setWeekCalenderDate(View view, Date date, TextView ymTextView, TextView[] wDate){ //??????????????? ???????????? ?????????
            firebaseAuth =  FirebaseAuth.getInstance();
            //??? ????????? ???
            ImageView[] weekCalendarDot = new ImageView[]{
                    view.findViewById(R.id.sun_dot),
                    view.findViewById(R.id.mon_dot),
                    view.findViewById(R.id.tue_dot),
                    view.findViewById(R.id.wed_dot),
                    view.findViewById(R.id.thu_dot),
                    view.findViewById(R.id.fri_dot),
                    view.findViewById(R.id.sat_dot),
            };

            Log.d("mytag","setWeekCalenderDate ?????? ??????");
            //?????? ?????? ??????
            SimpleDateFormat todaySdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA); //?????? ?????? ?????? ??????
            todaySdf.format(date); //?????? ?????? ??????

            //?????? ????????? ???????????? ??????
            Calendar cal = Calendar.getInstance(Locale.KOREA);
            cal.setFirstDayOfWeek(Calendar.SUNDAY);

            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            SimpleDateFormat simpleDate = new SimpleDateFormat("MM");
            String month = simpleDate.format(mDate);

            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, (-(dayOfWeek - 1)));

            //0000??? 00??? ????????? ??????
            ymTextView.setText(todaySdf.format(cal.getTime()).substring(0,4)+"??? "+todaySdf.format(cal.getTime()).substring(5,7)+"???");

            for ( int i = 0; i < 7; i++ ) {
                //00??? ????????? ??????
                wDate[i].setText(todaySdf.format(cal.getTime()).substring(8));
                cal.add(Calendar.DAY_OF_MONTH, 1);

                String monthValue;
                if(cal.get(Calendar.MONTH)<10) monthValue = "0"+cal.get(Calendar.MONTH);
                else monthValue = cal.get(Calendar.MONTH)+"";
                String dayValue;
                if(cal.get(Calendar.DAY_OF_MONTH)<10) dayValue = "0"+cal.get(Calendar.DAY_OF_MONTH);
                else dayValue = cal.get(Calendar.DAY_OF_MONTH)+"";
                //?????? ?????????????????? ????????????????????? ????????? ?????????~ ??????????????????
                Log.d("myapp","????????? : "+cal.get(Calendar.YEAR)+monthValue+dayValue);
            }

            //??? ~ ???
            for(int i=0;i<=6;i++){
                isDataExist = false;
                String checkDate = todaySdf.format(cal.getTime()).substring(0,4)+""+todaySdf.format(cal.getTime()).substring(5,7)+""+wDate[i].getText();

                int finalI = i;
                myRef.child(uid).child("date").child(checkDate).child("0").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        get_symptom = snapshot.child("symptom").getValue(String.class);
                        Log.d("myappp",checkDate+" ??????"+get_symptom);
                        if(!(get_symptom.equals("e"))){
                            weekCalendarDot[finalI].setVisibility(View.VISIBLE);
                            Log.d("myappp2",get_symptom);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                /*for(int j = 1; j <= cal.getMaximum(Calendar.DAY_OF_MONTH); j++){
                    fire_date = String.valueOf(j);
                    if((int)(Math.log10(j)+1) == 1) fire_date = "0"+fire_date;
                    fire_date = "2022" + month +  fire_date;
                    Log.d("myapp","fire_date : "+fire_date);
                    for(int k=0; k<5; k++){
                        myRef.child(uid).child("date").child(fire_date).child(String.valueOf(k)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String get_symptom = snapshot.child("symptom").getValue(String.class);
                                Log.d("myappp",fire_date+" ??? "+checkDate+" ??????");

                                if(!get_symptom.equals("e") && fire_date.equals(checkDate)){
                                    isDataExist = true;
                                    Log.d("trrr", String.valueOf(isDataExist));
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
                if(isDataExist) weekCalendarDot[i].setVisibility(View.VISIBLE);*/
            }
        }
    }
    //???????????? ??????
    public class InfoDialog{
        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //dialog.setContentView(R.layout.info_popup);
            dialog.setContentView(R.layout.info_popup);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.show();
        }
    }
}

