package de.hanneseilers.qrrallye;

import com.google.gson.Gson;

public class SnippetResponse {

	private RallyeInformation rallye;
	private String snippet;
	
	public SnippetResponse(RallyeInformation aRallye, String aSnippet) {
		rallye = aRallye;
		snippet = aSnippet;
	}
	
	public String toString(){
		return (new Gson()).toJson(this);
	}

	/**
	 * @return the rallye
	 */
	public RallyeInformation getRallye() {
		return rallye;
	}

	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}
	
}
