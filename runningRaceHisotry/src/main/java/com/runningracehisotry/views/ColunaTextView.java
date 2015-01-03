/**
 * 
 */
package com.runningracehisotry.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author nvhaiwork
 *
 */
public class ColunaTextView extends TextView {

	public ColunaTextView(Context paramContext) {
		super(paramContext);
		setFont();
	}

	public ColunaTextView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		setFont();
	}

	public ColunaTextView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		setFont();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#setPressed(boolean)
	 */
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if (pressed) {

			setAlpha(0.7f);
		} else {

			setAlpha(1f);
		}

		super.setPressed(pressed);
	}

	private void setFont() {

		Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),
				"font/Coluna.otf");
		setTypeface(typeFace);
	}

}
