package main;

import java.util.*;

public class Process {
	private int ID;
	private ProcessState state;
	private ArrayList<Integer> memoryBoundaries=new ArrayList<Integer>();
	private int arrivalTime;
	private int instructionCounter; 
	private int PC;
	private ArrayList<String> variables= new ArrayList<String>();
	private ArrayList<String> tempVariables= new ArrayList<String>();
	public Process(int ID,int arrivalTime) {
		this.ID=ID;
		this.state=ProcessState.NEW;
		this.setArrivalTime(arrivalTime);
		this.setPC(6);
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
		
	}
	public ProcessState getState() {
		return this.state;
	}
	public void setState(ProcessState state) {
		this.state = state;
		
	}
    public ArrayList<Integer> getMB() {
		return this.memoryBoundaries;
	}
	public void setMB(ArrayList<Integer> memoryBoundaries) {
		this.memoryBoundaries = memoryBoundaries;
		
	}
	public int getInstructionCounter() {
		return instructionCounter;
	}
	public void setInstructionCounter(int instructionCounter) {
		this.instructionCounter = instructionCounter;
	}
	public int getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getPC() {
		return PC;
	}
	public void setPC(int pC) {
		PC = pC;
	}
	public ArrayList<String> getVariables() {
		return variables;
	}
	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}
	public ArrayList<String> getTempVariables() {
		return tempVariables;
	}
	public void setTempVariables(ArrayList<String> tempVariables) {
		this.tempVariables = tempVariables;
	}
}
