package com.example.runspyrun;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private PoiBean hackIn;
	private PoiBean hackOut;
	private ArrayList<PoiBean> landMines = new ArrayList<PoiBean>();
	private String name;
	
	public Course(String name, PoiBean hackIn, PoiBean hackOut, ArrayList<PoiBean> landMines) {
		this.name = name;
		this.hackIn = hackIn;
		this.hackOut = hackOut;
		this.landMines = landMines;
	}
	
	public Course() {
		this.hackIn = null;
		this.hackOut = null;
		this.name = null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public PoiBean getHackInPoint() {
		return hackIn;
	}
	
	public void setHackInPoint(PoiBean hackIn) {
		this.hackIn = hackIn;
	}
	
	public PoiBean getHackOutPoint() {
		return hackOut;
	}
	
	public void setHackOutPoint(PoiBean hackOut) {
		this.hackOut = hackOut;
	}
	
	public ArrayList<PoiBean> getLandMines() {
		return landMines;
	}
	
	public void addMine(PoiBean bean) {
		landMines.add(bean);
	}
	
}
