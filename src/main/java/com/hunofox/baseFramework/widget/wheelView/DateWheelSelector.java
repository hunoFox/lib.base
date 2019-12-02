package com.hunofox.baseFramework.widget.wheelView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hunofox.baseFramework.R;
import com.hunofox.baseFramework.widget.wheelView.adapters.ArrayWheelAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 项目名称：ysApprove
 * 项目作者：胡玉君
 * 创建日期：2017/5/23 9:18.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class DateWheelSelector extends Dialog implements View.OnClickListener, OnWheelChangedListener {

    // 当前时间应传入对应参数
    private String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    private String currentMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);//month的索引是从0开始的，仅仅是month
    private String currentDay = String.valueOf(Calendar.getInstance().get(Calendar.DATE));

    private CharSequence title = "日期选择";
    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvConfirm;

    private WheelView wvYears;
    private WheelView wvMonth;
    private WheelView wvDay;

    private WeakReference<Activity> reference;

    private final List<String> years = new ArrayList<>();
    private final List<String> months = new ArrayList<>();
    private final List<String> days = new ArrayList<>();

    public DateWheelSelector(@NonNull Context context, int startYear, int endYear) {
        super(context, R.style.blend_theme_dialog);

        int finalYear = endYear;
        if(endYear < startYear){
            finalYear = Calendar.getInstance().get(Calendar.YEAR);
        }
        // 年份初始化应传入参数
        for (int i = startYear; i <= finalYear; i++) {
            years.add(String.valueOf(i));
        }

        for (int i = 1; i < 13; i++) {
            months.add(String.valueOf(i));
        }

        if (context instanceof Activity)
            reference = new WeakReference<>((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reference == null) return;
        if (reference.get() == null) return;
        initDialogParams();

        View view = LayoutInflater.from(reference.get()).inflate(R.layout.dialog_date_wheel, null);
        setContentView(view);
        initViewDatasAndListeners(view);
    }

    /** 初始化布局、数据及监听事件 */
    private void initViewDatasAndListeners(View view) {
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvConfirm = view.findViewById(R.id.tv_confirm);

        wvYears = view.findViewById(R.id.wv_years);
        wvYears.setVisibility(wvYearVisible? View.VISIBLE: View.GONE);
        wvMonth = view.findViewById(R.id.wv_month);
        wvMonth.setVisibility(wvMonthVisible? View.VISIBLE: View.GONE);
        wvDay = view.findViewById(R.id.wv_day);
        wvDay.setVisibility(wvDayVisible? View.VISIBLE: View.GONE);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        wvYears.addChangingListener(this);
        wvMonth.addChangingListener(this);
        wvDay.addChangingListener(this);
        wvYears.setVisibleItems(5);
        wvMonth.setVisibleItems(5);
        wvDay.setVisibleItems(5);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(listener != null){
                    listener.onCanceled(flag);
                }
                dismiss();
            }
        });

        ArrayWheelAdapter<String> adapterYears = new ArrayWheelAdapter<>(reference.get(), years.toArray(new String[years.size()]));
        wvYears.setViewAdapter(adapterYears);
        setCurrentYear(currentYear);
        ArrayWheelAdapter<String> adapterMonths = new ArrayWheelAdapter<>(reference.get(), months.toArray(new String[months.size()]));
        wvMonth.setViewAdapter(adapterMonths);
        setCurrentMonth(currentMonth);
        setDayDataByYearAndMonth();
    }

    /** 根据当前年份、月份设置日期 */
    private void setDayDataByYearAndMonth(){
        int year;
        int month;
        try{
            year = Integer.parseInt(currentYear);
            month = Integer.parseInt(currentMonth);
        }catch (Exception e){
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
        }
        if(month == 2){
            if((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
                initDays(29);
            else
                initDays(28);
        }else if(month == 4 || month == 6 || month == 9 || month == 11){
            initDays(30);
        }else{
            initDays(31);
        }
    }

    /** 设置日期 */
    private void initDays(int maxDay) {
        days.clear();
        for (int i = 1; i <= maxDay; i++) {
            days.add(String.valueOf(i));
        }
        /**初始化数据（日）*/
        ArrayWheelAdapter<String> adapterDay = new ArrayWheelAdapter<>(reference.get(), days.toArray(new String[days.size()]));
        wvDay.setViewAdapter(adapterDay);
        setCurrentDay(currentDay);
    }

    /**
     * 初始化对话框的各项参数
     */
    private void initDialogParams() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//获取窗口可视区域大小

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)this.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;//设置弹框与屏幕宽度一致
        lp.dimAmount = 0.5f;//蒙层
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        if(v == tvConfirm){
            if(listener != null){
                listener.onSelected(Integer.parseInt(currentYear),
                        Integer.parseInt(currentMonth),
                        Integer.parseInt(currentDay),
                        currentYear+"-"+((Integer.parseInt(currentMonth)<10)?("0"+currentMonth):currentMonth)+"-"+((Integer.parseInt(currentDay)<10)?("0"+currentDay):currentDay),
                        flag);
            }
            dismiss();
        }else if(v == tvCancel){
            if(listener != null){
                listener.onCanceled(flag);
            }
            dismiss();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(wheel == wvYears){
            currentYear = years.get(newValue);
            setDayDataByYearAndMonth();
        }else if(wheel == wvMonth){
            currentMonth = months.get(newValue);
            setDayDataByYearAndMonth();
        }else if(wheel == wvDay){
            currentDay = days.get(newValue);
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        this.title = title;
        if(tvTitle != null){
            tvTitle.setText(title);
        }
    }

    private OnConfirmSelectedListener listener;
    private String flag;
    public interface OnConfirmSelectedListener{
        void onSelected(int year, int month, int day, String formatDate, String flag);
        void onCanceled(String flag);
    }
    public void setOnConfirmSelectedListener(OnConfirmSelectedListener listener){
        this.listener = listener;
    }
    public void show(String flag) {
        show();
        this.flag = flag;
    }
    @Override
    public void show() {
        super.show();
        flag = null;
    }

    /** 设置当前年份 */
    public void setCurrentYear(String currentYear){
        this.currentYear = currentYear;
        try{
            for(int y = 0; y < years.size(); y++){
                if(years.get(y).equals(currentYear)){
                    wvYears.setCurrentItem(y);
                    break;
                }
            }
        }catch (Exception e){}
    }
    public void setCurrentMonth(String currentMonth){
        this.currentMonth = currentMonth;
        try{
            for(int m = 0; m < months.size(); m++){
                if(months.get(m).equals(currentMonth)){
                    wvMonth.setCurrentItem(m);
                    break;
                }
            }
        }catch (Exception e){}
    }
    public void setCurrentDay(String currentDay){
        this.currentDay = currentDay;
        try{
            if(("29".equals(currentDay) && days.size() < 29) || ("31".equals(currentDay) && days.size() < 31)){
                wvDay.setCurrentItem(days.size()-1);
            }else{
                for(int d = 0; d < days.size(); d++){
                    if(days.get(d).equals(currentDay)){
                        wvDay.setCurrentItem(d);
                        break;
                    }
                }
            }
        } catch (Exception e){}
    }

    private boolean wvYearVisible = true;
    private boolean wvMonthVisible = true;
    private boolean wvDayVisible = true;
    public void setWvYearVisible(boolean visible){
        this.wvYearVisible = visible;
        if(wvYears != null){
            wvYears.setVisibility(visible? View.VISIBLE: View.GONE);
        }
    }
    public void setWvMonthVisible(boolean visible) {
        this.wvMonthVisible = visible;
        if(wvMonth != null){
            wvMonth.setVisibility(visible? View.VISIBLE: View.GONE);
        }
    }
    public void setWvDayVisible(boolean visible) {
        this.wvDayVisible = visible;
        if(wvDay != null){
            wvDay.setVisibility(visible? View.VISIBLE: View.GONE);
        }
    }
}
