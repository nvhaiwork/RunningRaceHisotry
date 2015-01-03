package com.runningracehisotry.models;

import com.parse.ParseObject;

public class ParseIntentModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ParseObject parse;

	public ParseIntentModel(ParseObject parse) {
		super();
		this.parse = parse;
	}

	public ParseObject getParse() {
		return parse;
	}

	public void setParse(ParseObject parse) {
		this.parse = parse;
	}
}
