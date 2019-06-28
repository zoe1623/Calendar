package com.zoe.calender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView ll = findViewById(R.id.ll);
        Calendar calendar = Calendar.getInstance();
        ll.setData(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH));
        ll.setOnMyClickListener(new CalendarView.OnMyClickListener() {
            @Override
            public void clickDay(int year, int month, int day) {
                showToast(MainActivity.this, year + "--" + month + "--" + day);
            }
        });
//        mShow.set(2019, 4 - 1,1, 1,1,1);
//        mCalendarView = new LinearLayout(this);
//        mCalendarView.setOrientation(LinearLayout.VERTICAL);
//        ll.addView(mCalendarView);
//        initCalendar();
    }



        private static Toast toast;
        public void showToast(Context context,String content) {
            if (toast == null) {
                toast = Toast.makeText(context.getApplicationContext(),content,Toast.LENGTH_SHORT);
            } else {
                toast.setText(content);
            }
            toast.show();
        }


    private LinearLayout mCalendarView;
    private void initCalendar(){
        int width = 70;
        int height = 60;
        List<CalenderBean> data = getData();
        mCalendarView.removeAllViews();
        mCalendarView.addView(getCalendarTitle(), new LinearLayout.LayoutParams(-1, height));

        LinearLayout w_ll = new LinearLayout(this);
        String[]weeks = {"日","一","二", "三", "四","五","六"};
        mCalendarView.addView(w_ll, new LinearLayout.LayoutParams(-1, height));
        for(int i = 0; i<7; i++){
            w_ll.addView(getWeekTextView(weeks[i]), new LinearLayout.LayoutParams(width, height));
        }

        LinearLayout linearLayout = new LinearLayout(this);
        CalenderBean first = data.get(0);
        mCalendarView.addView(linearLayout, new LinearLayout.LayoutParams(-1, height));
        for(int i = 0; i<first.day_week-1; i++){
            linearLayout.addView(getTextView(null), new LinearLayout.LayoutParams(width, height));
        }

        int week = 1;
        for(CalenderBean bean: data){
            if(week != bean.week){
                week++;
                linearLayout = new LinearLayout(this);
                mCalendarView.addView(linearLayout, new LinearLayout.LayoutParams(-1, height));
            }
            linearLayout.addView(getTextView(bean), new LinearLayout.LayoutParams(width, height));
        }
    }

    private TextView mid;
    private View getCalendarTitle(){
        FrameLayout fl = new FrameLayout(this);

        mid = new TextView(this);
        mid.setText(mShow.get(Calendar.YEAR) + "年" + (1 +mShow.get(Calendar.MONTH)) + "月");
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(-2, -2);
        params1.gravity = Gravity.CENTER_HORIZONTAL;
        fl.addView(mid, params1);


        TextView last = new TextView(this);
        last.setText("<");
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, ((TextView)v).getText().toString(), Toast.LENGTH_SHORT).show();
                mShow.add(Calendar.MONTH,-1);
                mid.setText(mShow.get(Calendar.YEAR) + "年" + (1 +mShow.get(Calendar.MONTH)) + "月");
                initCalendar();
            }
        });
        fl.addView(last);

        TextView next = new TextView(this);
        next.setText(">");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, ((TextView)v).getText().toString(), Toast.LENGTH_SHORT).show();
                mShow.add(Calendar.MONTH,1);
                int year = mShow.get(Calendar.YEAR);
                int month = mShow.get(Calendar.MONTH);
                Calendar today = Calendar.getInstance();
                if(year > today.get(Calendar.YEAR)){
                    mShow.add(Calendar.MONTH,-1);
                    return;
                }
                if( year == today.get(Calendar.YEAR) && month > today.get(Calendar.MONTH)){
                    mShow.add(Calendar.MONTH,-1);
                    return;
                }
                mid.setText(year + "年" + (1 + month) + "月");
                initCalendar();
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.RIGHT;
        fl.addView(next, params);
        fl.setBackgroundColor(Color.BLUE);
        return fl;
    }

    private View getTextView(final CalenderBean bean){
        TextView textView = new TextView(this);
        if(bean == null)return textView;
        textView.setText(bean.day+"");
        textView.setTextColor(bean.color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
        textView.setGravity(Gravity.CENTER);
        textView.setTag(bean);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalenderBean tag = (CalenderBean) v.getTag();
                Toast.makeText(MainActivity.this, tag.year + "-" + tag.month + "-" + tag.day, Toast.LENGTH_SHORT).show();
            }
        });
        return textView;
    }
    private View getWeekTextView(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
//        textView.setTextColor(bean.color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    Calendar mShow = Calendar.getInstance();
    private List<CalenderBean> getData(){
        Calendar calendar = (Calendar) mShow.clone();
        List<CalenderBean> list = new ArrayList<>();
        int month = calendar.get(Calendar.MONTH);
        Calendar today = Calendar.getInstance();
        while (true){
            CalenderBean bean = new CalenderBean();
            bean.day = calendar.get(Calendar.DAY_OF_MONTH);
            bean.week = calendar.get(Calendar.WEEK_OF_MONTH);
            bean.year = calendar.get(Calendar.YEAR);
            bean.day_week = calendar.get(Calendar.DAY_OF_WEEK);
            bean.month = month + 1;
            if(bean.year > today.get(Calendar.YEAR)){
                bean.color = Color.GRAY;
            }else if(bean.year == today.get(Calendar.YEAR) && bean.month > today.get(Calendar.MONTH) + 1){
                bean.color = Color.GRAY;
            }else if(bean.year == today.get(Calendar.YEAR) && bean.month == today.get(Calendar.MONTH) + 1
                    &&bean.day > today.get(Calendar.DAY_OF_MONTH)){
                bean.color = Color.GRAY;
            }else {
                bean.color = Color.WHITE;
            }


            list.add(bean);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if(month != calendar.get(Calendar.MONTH)){
                break;
            }
        }
        return list;
    }
}
