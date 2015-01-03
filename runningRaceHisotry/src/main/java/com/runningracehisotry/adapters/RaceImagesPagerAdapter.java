/**
 * 
 */
package com.runningracehisotry.adapters;

import java.util.List;

import com.runningracehisotry.fragments.RaceImagesFragment;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * @author nvhaiwork
 *
 */
public class RaceImagesPagerAdapter extends FragmentStatePagerAdapter {

	private List<Bitmap> mImages;

	/**
	 * @param fm
	 */
	public RaceImagesPagerAdapter(FragmentManager fm, List<Bitmap> images) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.mImages = images;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return RaceImagesFragment.getInstance(mImages.get(position));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		if (mImages != null) {

			return mImages.size();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	 */
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
}
