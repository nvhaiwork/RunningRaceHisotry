/**
 *
 */
package com.runningracehisotry.models;

import java.io.Serializable;

/**
 * @author ubuntu
 * 
 */
public class BaseModel implements Serializable, Cloneable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	public BaseModel() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public BaseModel clone() {
		// TODO Auto-generated method stub
		try {

			return (BaseModel) super.clone();
		} catch (CloneNotSupportedException ce) {

			ce.printStackTrace();
		}

		return null;
	}
}
