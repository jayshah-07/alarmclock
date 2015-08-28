package com.example.matthew.alarmclock;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.matthew.alarmclock.swipetodismiss.ItemTouchHelperAdapter;
import com.example.matthew.alarmclock.swipetodismiss.ItemTouchHelperViewHolder;
import com.example.matthew.alarmclock.swipetodismiss.OnStartDragListener;
import com.example.matthew.alarmclock.swipetodismiss.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MasterView extends ActionBarActivity{

    private RecyclerView mRecyclerView;
    private AlarmListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_view);

        /*Actionbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*RecyclerView*/
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Specify animator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter
        mAdapter = new AlarmListAdapter(dbHelper.getAlarms());
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mFAB = (FloatingActionButton) findViewById(R.id.fab);

        mFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startAlarmDetailsActivity(-1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            mAdapter.setAlarms(dbHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAlarmEnabled(long id, boolean isEnabled){
        AlarmModel model = dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        dbHelper.updateAlarm(model);
        //Refresh the adapter
        mAdapter.setAlarms(dbHelper.getAlarms());
        mAdapter.notifyDataSetChanged();
    }

    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder{

        private TextView mAlarmTimeHours;
        private TextView mAlarmTimeMinutes;
        private TextView mAlarmName;
        private TextView mAlarmDays;
        private SwitchCompat mAlarmActive;
        private boolean isTouched = false;

        private AlarmModel mAlarm;

        public ViewHolder(View v) {
            super(v);

            mAlarmTimeHours = (TextView) v.findViewById(R.id.alarm_time_hours);
            mAlarmTimeMinutes = (TextView) v.findViewById(R.id.alarm_time_minutes);
            mAlarmName = (TextView) v.findViewById(R.id.alarm_name);
            mAlarmDays = (TextView) v.findViewById(R.id.alarm_days);
            mAlarmActive = (SwitchCompat) v.findViewById(R.id.alarm_active);

            //Set tag and onClick of switch

            mAlarmActive.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTouched = true;
                    return false;
                }
            });

            mAlarmActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isTouched){
                        isTouched  = false;
                        long id = (Long)buttonView.getTag();
                        setAlarmEnabled(id, isChecked);
                    }

                }
            });


            //Set tag and onClick of whole view

            itemView.setOnClickListener(this);
        }

        public void bindAlarm(AlarmModel alarm){
            mAlarm = alarm;

            mAlarmActive.setChecked(mAlarm.isEnabled);

            mAlarmActive.setTag(Long.valueOf(mAlarm.id));
            itemView.setTag(Long.valueOf(mAlarm.id));

            //Get string format value of time
            String hour = alarm.timeHour < 10 ? "0" + alarm.timeHour : String.valueOf(alarm.timeHour);
            String min = alarm.timeMinute < 10 ? "0" + alarm.timeMinute : String.valueOf(alarm.timeMinute);

            mAlarmTimeHours.setText(hour);
            mAlarmTimeMinutes.setText(min);

            mAlarmName.setText(alarm.name);

            String days = "";
            for(int i = 0; i < 7; i++) {
                if(alarm.getRepeatingDay(i)){
                    switch(i) {
                        case AlarmModel.MONDAY: days += "Mon, ";
                            break;
                        case AlarmModel.TUESDAY: days += "Tue, ";
                            break;
                        case AlarmModel.WEDNESDAY: days += "Wed, ";
                            break;
                        case AlarmModel.THURSDAY: days += "Thu, ";
                            break;
                        case AlarmModel.FRIDAY: days += "Fri, ";
                            break;
                        case AlarmModel.SATURDAY: days += "Sat, ";
                            break;
                        case AlarmModel.SUNDAY: days += "Sun, ";
                            break;
                    }
                }
            }

            days = days.substring(0, days.length()-2);
            mAlarmDays.setText(days);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), String.valueOf(itemView.getTag()), Toast.LENGTH_LONG).show();
            startAlarmDetailsActivity(((Long) itemView.getTag()).longValue());

        }

        @Override
        public void onItemSelected() {
            //itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    private class AlarmListAdapter extends RecyclerView.Adapter<ViewHolder> implements ItemTouchHelperAdapter{

        private List<AlarmModel> mAlarms;

        public AlarmListAdapter(List<AlarmModel> alarms){
            mAlarms = alarms;
        }

        public void setAlarms(List<AlarmModel> alarms){
            mAlarms = alarms;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        /* create a new view */
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.alarm_list_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if(mAlarms != null) {
                AlarmModel currentAlarm = mAlarms.get(position);
                holder.bindAlarm(currentAlarm);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if(mAlarms != null) {
                return mAlarms.size();
            }
            return 0;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {}

        @Override
        public void onItemDismiss(int position) {

            AlarmModel currentAlarm = mAlarms.get(position);
            long currentAlarmId = Long.valueOf(currentAlarm.id);
            dbHelper.deleteAlarm(currentAlarmId);
            mAlarms.remove(position);

            //Refresh adapter
            mAdapter.setAlarms(dbHelper.getAlarms());
            notifyItemRemoved(position);
        }
    }

}
