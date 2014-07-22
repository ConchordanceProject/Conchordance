package com.conchordance.instrument;

/**
 * Class to model a capo and its position on the neck of an instrument.
 * 
 */
public class Capo {
	
	/**
	 * Get the fret this capo is placed on.
	 * @return the fret this capo is placed on.
	 */
	public int getFret() {
		return fret;
	}
	
	/**
	 * Set the fret this capo is placed on.
	 * @param fret the fret to place the capo on.
	 */
	public void setFret(int fret) {
		this.fret = fret;
	}
	
	/**
	 * Get the lowest-numbered string the capo is affecting.
	 * @return the lowest-numbered string the capo is affecting
	 */
	public int getLowestString() {
		return lowestString;
	}
	
	/**
	 * Set the lowest-numbered string the capo is affecting.
	 * @param string the lowest-numbered string this capo is affecting.
	 */
	public void setLowestString(int string) {
		lowestString = string;
	}

	/**
	 * Get the highest-numbered string the capo is affecting.
	 * @return the highest-numbered string the capo is affecting
	 */
	public int getHighestString() {
		return highestString;
	}

	/**
	 * Set the highest-numbered string the capo is affecting.
	 * @param string the highest-numbered string this capo is affecting.
	 */
	public void setHighestString(int string) {
		highestString = string;
	}

	/**
	 * Constructor.
	 * Initialize the capo with its position on the neck.
	 * @param fret the fret the capo is set at.
	 * @param lowestString the lowest-numbered fret affected by the capo
	 * @param highestString the highest-numbered fret affected by the capo
	 */
	public Capo(int fret, int lowestString, int highestString) {
		this.fret = fret;
		this.lowestString = lowestString;
		this.highestString = highestString;
	}
	
	private int fret;
	private int lowestString;
	private int highestString;
}
