package main;

import java.io.IOException;
import java.util.*;
public class Schedueler {
private static Queue<Process> readyQueue;
private static Queue<Process> blockedQueue;

 
 public Schedueler() {
	this.readyQueue= new LinkedList<Process>();
	this.blockedQueue= new LinkedList<Process>();
	
 }
 public void execute(ArrayList<Process> processes, int timeSlice,Interpreter intr) throws IOException {
	    int cycle=0;
	    Process current = null;
	    while (!readyQueue.isEmpty() || !processes.isEmpty()) {
	    	for(int i=0;i<processes.size();i++) {
	    		if(processes.get(i).getArrivalTime()==cycle) {
	    			readyQueue.add(processes.get(i));
	    			changeState(processes.get(i),"READY",intr);
	    			processes.remove(processes.get(i));
	    		}
	    	}
	    	System.out.println("Ready Queue:");
	    	printQueue(readyQueue);
	    	System.out.println("Blocked Queue:");
	    	printQueue(blockedQueue);
	    	current=readyQueue.remove();
	    
	    	for(int j=0;j<timeSlice;j++) {
	    	System.out.println("Cycle: "+cycle);
	    	if(j!=0) {
	    	for(int i=0;i<processes.size();i++) {
	    		if(processes.get(i).getArrivalTime()==cycle) {
	    			readyQueue.add(processes.get(i));
	    			changeState(processes.get(i),"READY",intr);
	    			processes.remove(processes.get(i));
	    		}
	    	}
	    	System.out.println("Ready Queue:");
	    	printQueue(readyQueue);
	    	System.out.println("Blocked Queue:");
	    	printQueue(blockedQueue);}
	    	if(current.getState()==ProcessState.BLOCKED) {
	    		System.out.println("Process "+current.getID()+" is blocked so will discontinue for now");
	    		break;
	    	}
	    	if(current.getState()==ProcessState.TERMINATED) {
	    	
	break;
	    	}
	    	EnsureInMemory(current,intr);
	    	if(current.getState()!=ProcessState.BLOCKED) {
	    	changeState(current,"RUNNING",intr);}
	    	
	    	
	    	//System.out.println("Cycle: "+cycle);
	    	System.out.println("Process "+current.getID()+" is currently executing.");
            String state= SystemCalls.execute(current,intr,this);
            updateBlocked(intr);
            changeState(current,state,intr);
	    	cycle++;
	    	System.out.println("Main Memory Content: ");
	    	printMemory(intr.getMainMemory());
	    	System.out.println("Disk Memory Content: ");
	    	printMemory(intr.getDiskMemory());}//print disk mem too
	    	if(current.getState()==ProcessState.RUNNING) { //law kanet blocked aw terminated khalas msh harag3ha el ready queue
	    		readyQueue.add(current);
	    		changeState(current,"READY",intr);

	    	}
	    	
	    	}  	
	    }
	private void updateBlocked(Interpreter intr) {
		for (Process process : blockedQueue) {
			changeState(process,"BLOCKED",intr);
		}
		for (Process process : readyQueue) {
			changeState(process,"READY",intr);
		}
	
}
	private static void printMemory(Memory mem) {
		String[] mem2= mem.getMemory();
		for(int i=0;i<40;i++) {
			System.out.println("Memory Word "+i+"="+mem2[i]);
		}
	
}
	private static void EnsureInMemory(Process current,Interpreter intr) {
	 boolean inMem=intr.getMainMemory().getElement(""+current.getID());
	 if(!inMem) {
		 System.out.println("Process "+current.getID()+" was swapped out of disk memory and into main memory");
		 intr.swapFromDiskToMem(current);
	 }
	}
	
	public static void addToReady(Process p) {
		readyQueue.add(p);
	}
	public static Queue<Process> getReadyQueue() {
		return readyQueue;
	}
	private static void changeState(Process P, String state,Interpreter intr) {
		ProcessState newstate=null;
		switch(state) {
		case "READY":newstate= ProcessState.READY; break;
		case "RUNNING":newstate= ProcessState.RUNNING; break;
		case "BLOCKED":newstate= ProcessState.BLOCKED; break;
		case "TERMINATED":newstate= ProcessState.TERMINATED; break;
		}
		P.setState(newstate);
		int start= P.getMB().get(0);
		String[] oldmemory=(intr.getMainMemory().getMemory());
		if(oldmemory[start].equals(""+P.getID())) {
		oldmemory[start+1]=state;}
		Memory newMem= intr.getMainMemory();
		String[] oldmemory2=(intr.getDiskMemory().getMemory());
		if(oldmemory2[start]!=null) {
		if(oldmemory2[start].equals(""+P.getID())) {
		oldmemory2[start+1]=state;}
		Memory newMem2= intr.getDiskMemory();
		newMem.setMemory(oldmemory);
		intr.setMainMemory(newMem);
		newMem2.setMemory(oldmemory2);
		intr.setDiskMemory(newMem2);
	}}

public static void printQueue(Queue<Process> ps) {
	if(!ps.isEmpty()) {
	System.out.println("The Queue elements are ordered as follows");
	for (Process process : ps) {
        System.out.println("Process "+process.getID());
    }}
}
public static Queue<Process> getBlockedQueue(){
	return blockedQueue;
}
public static void setBlockedQueue(Queue<Process> newQ){
	blockedQueue=newQ;
}
public static void setReadyQueue(Queue<Process> temp) {
	readyQueue=temp;
	
}

}
