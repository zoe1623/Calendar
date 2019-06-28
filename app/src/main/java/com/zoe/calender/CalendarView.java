package com.zoe.calender;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zz on 2019/4/18.
 */

public class CalendarView extends LinearLayout {
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public static String[]weeks = {"日","一","二", "三", "四","五","六"};
    private Calendar mShow = Calendar.getInstance();
    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        initCalendar();
    }

    public void setData(int year, int month){
        mShow.set(year, month,1, 1,1,1);
        initCalendar();
    }

    private void initCalendar(){
        initCalendar(true);
    }
    private void initCalendar(boolean right){
        int width = mWidth/7;
        int height = width - 10;
        List<CalenderBean> data = getData();
        removeAllViews();
        LayoutParams params = new LayoutParams(-1, height);
        params.topMargin = dp2px(10);
        params.leftMargin = dp2px(10);
        params.rightMargin = dp2px(10);
        addView(getCalendarTitle(), params);

        LinearLayout w_ll = new LinearLayout(getContext());
        addView(w_ll, new LinearLayout.LayoutParams(-1, height));
        for(int i = 0; i<7; i++){
            w_ll.addView(getWeekTextView(weeks[i]), new LinearLayout.LayoutParams(width, height));
        }

        LinearLayout linearLayout = new LinearLayout(getContext());
        CalenderBean first = data.get(0);
        addView(linearLayout, new LinearLayout.LayoutParams(-1, height));
        for(int i = 0; i<first.day_week-1; i++){
            linearLayout.addView(getTextView(null), new LinearLayout.LayoutParams(width, height));
        }

        int week = 1;
        for(CalenderBean bean: data){
            if(week != bean.week){
                week++;
                linearLayout = new LinearLayout(getContext());
                addView(linearLayout, new LinearLayout.LayoutParams(-1, height));
            }
            linearLayout.addView(getTextView(bean), new LinearLayout.LayoutParams(width, height));
        }
        if(right) isToday();
    }

    private TextView mid;
    private ImageView next;
    private View getCalendarTitle(){
        FrameLayout fl = new FrameLayout(getContext());

        mid = new TextView(getContext());
        mid.setText(mShow.get(Calendar.YEAR) + "年" + (1 +mShow.get(Calendar.MONTH)) + "月");
        mid.setTextColor(getResources().getColor(R.color.c_gray_ff979ec2));
        mid.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(-2, -2);
        params1.gravity = Gravity.CENTER;
        fl.addView(mid, params1);


        ImageView last = new ImageView(getContext());
        last.setImageResource(R.drawable.icon_history_pre);
        last.setPadding(dp2px(10),dp2px(10),dp2px(10),dp2px(10));
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShow.add(Calendar.MONTH,-1);
                mid.setText(mShow.get(Calendar.YEAR) + "年" + (1 +mShow.get(Calendar.MONTH)) + "月");
                initCalendar(false);
            }
        });
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(dp2px(40), dp2px(40));
        params2.gravity = Gravity.CENTER_VERTICAL;
        fl.addView(last, params2);

        next = new ImageView(getContext());
        next.setImageResource(R.drawable.icon_history_next);
        next.setPadding(dp2px(10),dp2px(10),dp2px(10),dp2px(10));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp2px(40), dp2px(40));
        params.gravity = Gravity.RIGHT| Gravity.CENTER_VERTICAL;
        fl.addView(next, params);
        fl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return fl;
    }

    private View getTextView(final CalenderBean bean){
        TextView textView = new TextView(getContext());
        if(bean == null)return textView;
        textView.setText(bean.day+"");
        textView.setTextColor(bean.color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
        textView.setGravity(Gravity.CENTER);
        textView.setTag(bean);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalenderBean tag = (CalenderBean) v.getTag();
                Calendar today = Calendar.getInstance();
                if(tag.year > today.get(Calendar.YEAR)){
                    return;
                }
                if(tag.year == today.get(Calendar.YEAR) && tag.month > today.get(Calendar.MONTH) + 1){
                    return;
                }
                if(tag.year == today.get(Calendar.YEAR) && tag.month == today.get(Calendar.MONTH) + 1
                        && tag.day > today.get(Calendar.DAY_OF_MONTH)){
                    return;
                }
                if(mListener != null){
                    mListener.clickDay(tag.year, tag.month, tag.day);
                }
            }
        });
        return textView;
    }
    private View getWeekTextView(String text){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.c_gray_ff979ec2));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

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
                bean.color = getResources().getColor(R.color.c_gray_3e4463);
            }else if(bean.year == today.get(Calendar.YEAR) && bean.month > today.get(Calendar.MONTH) + 1){
                bean.color = getResources().getColor(R.color.c_gray_3e4463);
            }else if(bean.year == today.get(Calendar.YEAR) && bean.month == today.get(Calendar.MONTH) + 1
                    &&bean.day > today.get(Calendar.DAY_OF_MONTH)){
                bean.color = getResources().getColor(R.color.c_gray_3e4463);
            }else {
                bean.color = getResources().getColor(R.color.c_gray_ff979ec2);
            }


            list.add(bean);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if(month != calendar.get(Calendar.MONTH)){
                break;
            }
        }
        return list;
    }

    private boolean isToday(){
        //iv_activity_detail_next
        Calendar today = Calendar.getInstance();
        if(mShow.get(Calendar.YEAR) > today.get(Calendar.YEAR)){
            if(next != null) next.setImageResource(R.drawable.icon_history_next_unable);
            return true;
        }
        if(mShow.get(Calendar.YEAR) == today.get(Calendar.YEAR) && mShow.get(Calendar.MONTH) >= today.get(Calendar.MONTH)){
            if(next != null) next.setImageResource(R.drawable.icon_history_next_unable);
            return true;
        }
        if(next != null) next.setImageResource(R.drawable.icon_history_next);
        return false;
    }

    private int mWidth = -1;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mWidth == -1) {
            mWidth = getMeasuredWidth()-dp2px(10);
            initCalendar();
        }
    }

    private int dp2px(int dp){
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    public void setOnMyClickListener(OnMyClickListener l){
        mListener = l;
    }
    private OnMyClickListener mListener;
    public interface OnMyClickListener{
        void clickDay(int year, int month, int day);
    }
}
