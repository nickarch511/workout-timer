package code;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Exercise implements Comparable<Exercise>{

	
	private String name; 
	private int length; // in seconds
	private int orderNum;
	
	public Exercise(String name, int length, int order) {
		this.name = name;
		this.length = length;
		this.orderNum = order;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (! (other instanceof Exercise)) {
			return false;
		}
		return (this.name.equals(((Exercise)other).name) && this.length == ((Exercise)other).length);
	}
	
	public String toString() {
		return name + " for " + length + "!";
	}
	
	public String textString() {
		return name + "\n" + length;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public int getOrder() {
		return this.orderNum;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public void setOrder(int order) {
		this.orderNum = order;
	}

	@Override
	public int compareTo(Exercise other) {
		return this.orderNum - other.orderNum;
	}
	
	
	
	
}
