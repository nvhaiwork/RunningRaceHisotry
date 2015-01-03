package com.runningracehisotry.fragments;

import com.runningracehisotry.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RaceImagesFragment extends Fragment {

	private Bitmap mBitmap;

	public static RaceImagesFragment getInstance(Bitmap image) {

		RaceImagesFragment fragment = new RaceImagesFragment();
		fragment.setImage(image);
		return fragment;
	}

	/**
	 * Set race information
	 * */
	private void setImage(Bitmap image) {

		this.mBitmap = image;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_image_dialog, container,
				false);
		ImageView image = (ImageView) view.findViewById(R.id.race_image);
		if (mBitmap != null) {

			image.setImageBitmap(mBitmap);
		}

		return view;
	}
}
