package com.example.matthew.alarmclock.swipetodismiss;

/**
 * Created by Matthew on 2015/07/17.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
