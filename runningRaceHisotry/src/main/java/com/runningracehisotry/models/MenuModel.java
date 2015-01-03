package com.runningracehisotry.models;

public class MenuModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dislayText;
	private int image;

	public void setImage(int image) {
		this.image = image;
	}

	public Integer getImage() {
		return this.image;
	}

	public String getDislayText() {
		return dislayText;
	}

	public void setDislayText(String dislayText) {
		this.dislayText = dislayText;
	}
}
