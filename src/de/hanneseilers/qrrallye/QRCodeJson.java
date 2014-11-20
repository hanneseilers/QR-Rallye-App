package de.hanneseilers.qrrallye;

import com.google.gson.Gson;

public class QRCodeJson {

	private String url;
	private int rID;
	private int n;
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the rallye ID
	 */
	public int getRallyeID() {
		return rID;
	}
	/**
	 * @return the snippedNumber
	 */
	public int getSnippetNumber() {
		return n;
	}
	
	public String toString(){
		return (new Gson()).toJson(this);
	}
	
}
