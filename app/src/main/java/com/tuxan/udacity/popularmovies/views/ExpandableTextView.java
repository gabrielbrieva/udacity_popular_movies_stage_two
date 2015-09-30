package com.tuxan.udacity.popularmovies.views;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ExpandableTextView extends TextView {

    //private static final String TAG = ExpandableTextView.class.getSimpleName();

    private static final int maximumLines = 2000;

    private Boolean mIsExpanded;
    private Integer mMaxLine;

    private static final String sMaximumVarName = "mMaximum";


    /**
     * @param context
     */
    public ExpandableTextView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        expand();
        collapse();
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ExpandableTextView etv = (ExpandableTextView)v;
                toggle(etv);
            }
        });
    }

    /**
     * @return is expanded or not
     */
    protected Boolean isExpanded() {
        return mIsExpanded;
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        //Log.e(TAG, "operation is not supported!");
    }

    /**
     * Toggle if it is expanded or not
     */
    public final void toggle() {
        toggle(this);
    }


    private final void toggle(ExpandableTextView etv) {
        if (etv.isExpanded()) {
            //Log.w(TAG, TAG + " is collapsed.");
            etv.collapse();
        } else {
            //Log.w(TAG, TAG + " is expanded.");
            etv.expand();
        }
    }

    public void collapse() {
        mIsExpanded = false;
        setMaxLines(mMaxLine);
    }

    public void expand() {
        mIsExpanded = true;
        if (mMaxLine == null) {
            storeMaxLine();
        }
        setMaxLines(maximumLines);
    }

    /**
     * Extract private maxLine from super class
     */
    private void storeMaxLine() {
        Field f;
        try {
            f = getClass().getSuperclass().getDeclaredField(sMaximumVarName);
            f.setAccessible(true);
            mMaxLine = f.getInt(this);

            f.setAccessible(false);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
    }

}
