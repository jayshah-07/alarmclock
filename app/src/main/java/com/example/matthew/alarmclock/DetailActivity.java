package com.example.matthew.alarmclock;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;


public class DetailActivity extends AppCompatActivity{

    private AlarmModel alarmDetails;

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);

    private LinearLayout timeSelection;
    public static TextView textViewHours;
    public static TextView textViewMin;

    private ToggleButton toggleMon;
    private ToggleButton toggleTue;
    private ToggleButton toggleWed;
    private ToggleButton toggleThu;
    private ToggleButton toggleFri;
    private ToggleButton toggleSat;
    private ToggleButton toggleSun;

    private EditText alarmName;

    private CheckBox checkBoxWeekly;

    private RelativeLayout melodySelection;
    public static TextView textViewMelody;

    private CheckBox checkBoxVibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*
            TOOLBAR
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //BACK BUTTON ON TOOLBAR
        if(null != toolbar){

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

            toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                }
            });

        }

        //INFLATE MENU FOR TOOLBAR
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }
                else if(id == R.id.action_save){

                    updateModelFromLayout();

                    AlarmManagerHelper.cancelAlarms(getApplicationContext());

                    if(alarmDetails.id < 0 ){
                        dbHelper.createAlarm(alarmDetails);
                    }
                    else{
                        dbHelper.updateAlarm(alarmDetails);
                    }

                    AlarmManagerHelper.setAlarms(getApplicationContext());

                    setResult(RESULT_OK);
                    finish();
                }
                else if(id == R.id.action_delete){
                    //Notify list adapter to Delete alarm
                    finish();
                }

                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu_detail);

        //Initialise components

        timeSelection = (LinearLayout) findViewById(R.id.time_selection);
        timeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        textViewHours = (TextView) findViewById(R.id.alarm_time_hours);
        textViewMin = (TextView) findViewById(R.id.alarm_time_min);
        toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);
        toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        alarmName = (EditText) findViewById(R.id.alarm_screen_name);
        checkBoxWeekly = (CheckBox) findViewById(R.id.checkbox_weekly);

        melodySelection = (RelativeLayout) findViewById(R.id.melody_selection);
        melodySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 1);
            }
        });

        textViewMelody = (TextView) findViewById(R.id.melody_title);
        checkBoxVibrate = (CheckBox) findViewById(R.id.checkbox_vibrate);

        //Handle intent

        long id = getIntent().getExtras().getLong("id");

        if(id == -1) {
            //NEW ALARM
            alarmDetails = new AlarmModel();
        }
        else{
            //EDIT EXISTING ALARM
            alarmDetails = dbHelper.getAlarm(id);

            //Time Picker

            int hour = alarmDetails.timeHour;
            int minute = alarmDetails.timeMinute;

            String h = String.valueOf(hour);
            String m = String.valueOf(minute);

            if(minute < 10){
                m = "0" + minute;
            }
            if(hour < 10){
                h = "0" + hour;
            }

            textViewHours.setText(h);
            textViewMin.setText(m);

            //Set days
            toggleMon.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            toggleTue.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            toggleWed.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            toggleThu.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            toggleFri.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
            toggleSat.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));
            toggleSun.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));

            //Alarm name

            alarmName.setText(alarmDetails.name);

            //Repeat weekly

            checkBoxWeekly.setChecked(alarmDetails.repeatWeekly);

            textViewMelody.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));

            //checkBoxVibrate.setChecked(alarmDetails);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch(requestCode){
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    textViewMelody.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
                    break;
                }
                default: {
                    break;
                }

            }
        }
    }

    public void updateModelFromLayout(){

        //Time
        alarmDetails.timeMinute = Integer.parseInt(textViewMin.getText() + "");
        alarmDetails.timeHour = Integer.parseInt(textViewHours.getText() + "");

        //Days
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, toggleMon.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, toggleTue.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, toggleWed.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, toggleThu.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, toggleFri.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, toggleSat.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, toggleSun.isChecked());

        //Alarm Name
        alarmDetails.name = alarmName.getText().toString();

        //RepeatWeekly
        alarmDetails.repeatWeekly = checkBoxWeekly.isChecked();

        alarmDetails.isEnabled = true;

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private int hours;
        private int minutes;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Use current time as default value
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Create new instance of TimePickerDialog

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Do something with the time chosen by the user

            String h = String.valueOf(hourOfDay);
            String m = String.valueOf(minute);

            if(minute < 10){
                m = "0" + minute;
            }
            if(hourOfDay < 10){
                h = "0" + hourOfDay;
            }

            textViewHours.setText(h);
            textViewMin.setText(m);
        }
    }
}
