package de.hanneseilers.qrrallye;

import com.google.gson.Gson;

public class SnippetResponse {

	private RallyeInformation rallye;
	private String snippet;
	private int itemsSolved;
	private int itemsTotal;
	
	public SnippetResponse(RallyeInformation aRallye, String aSnippet, int aItemsSolved, int aItemsTotal) {
		rallye = aRallye;
		snippet = aSnippet;
		itemsSolved = aItemsSolved;
		itemsTotal = aItemsTotal;
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

	/**
	 * @return the itemsSolved
	 */
	public int getItemsSolved() {
		return itemsSolved;
	}

	/**
	 * @return the itemsTotal
	 */
	public int getItemsTotal() {
		return itemsTotal;
	}
	
}
