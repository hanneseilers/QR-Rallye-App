package de.hanneseilers.qrrallye;

import com.google.gson.Gson;

public class RallyeInformation {

	private int rID;
	private String rName;
	private long rSnippetsDelay;
	
	public String toString(){
		return (new Gson()).toJson(this);
	}

	/**
	 * @return the ID
	 */
	public int getID() {
		return rID;
	}

	/**
	 * @return the Name
	 */
	public String getName() {
		return rName;
	}

	/**
	 * @return the SnippetsDelay
	 */
	public long getSnippetsDelay() {
		return rSnippetsDelay;
	}
	
}
