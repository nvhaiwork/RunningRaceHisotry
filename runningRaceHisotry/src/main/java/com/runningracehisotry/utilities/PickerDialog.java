package com.runningracehisotry.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.runningracehisotry.R;

/**
 * Created by QuyNguyen on 5/23/2015.
 */
public class PickerDialog extends Dialog {

    private String[] mValueArr;
    private NumberPicker mAgepicker;
    private TextView doneTxt, cancelTxt;
    private OnDoneClickListener mListener;
    private OnCancelClickListener cancelListener;
    private int mMinValue;
    private int mMaxValue;

    public PickerDialog(Context context, String[] valueArr, int min, int max) {
        super(context);

        mValueArr = valueArr;
        mMinValue = min;
        mMaxValue = max;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_list_group);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        setCancelable(true);

        doneTxt = (TextView) findViewById(R.id.done_txt);
        doneTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {

                    mListener.OnDone(mAgepicker.getValue(),
                            mValueArr[mAgepicker.getValue()]);
                }
            }
        });
        cancelTxt = (TextView) findViewById(R.id.cancel_txt);
        cancelTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelListener != null) {

                    cancelListener.OnCancel();
                }
            }
        });

        mAgepicker = (NumberPicker) findViewById(R.id.age_picker);
        mAgepicker.setDisplayedValues(mValueArr);
        mAgepicker.setMinValue(mMinValue);
        mAgepicker.setMaxValue(mMaxValue);
        mAgepicker
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }



    /**
     * set minValue for picker
     *
     * @param minValue
     */
    public void setMinValue(int minValue) {

        if (mAgepicker != null) {

            mAgepicker.setMinValue(minValue);
        }
    }

    /**
     * set maxValue for picker
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {

        if (mAgepicker != null) {

            mAgepicker.setMaxValue(maxValue);
        }
    }
    public void setOnDoneClickListener(OnDoneClickListener listener) {

        mListener = listener;
    }
    public void setOnCancelClickListener(OnCancelClickListener listener) {

        cancelListener = listener;
    }

    public interface OnDoneClickListener {

        public void OnDone(int position, String value);
    }
    public interface OnCancelClickListener {

        public void OnCancel();
    }
}
