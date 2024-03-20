package main;

import java.util.*;

import main.Process;

public class Mutex {
   Queue<Process> blockedQueue;
   public Process processUsing;
  
   public Mutex() {
	   this.blockedQueue=  new LinkedList<Process>();
	   processUsing=null;
	  
   }
   public boolean semWait(Process process,Schedueler s) {
	   if(processUsing==null||process.getID()==processUsing.getID()) {
		   processUsing=process;
		   return false;
	   }else {
		   if(process.getState()!=ProcessState.BLOCKED) {
		   process.setState(ProcessState.BLOCKED);
		   blockedQueue.add(process);
		   Queue<Process> temp=  s.getBlockedQueue();
		   temp.add(process);
		   s.setBlockedQueue(temp);
		   Queue<Process> temp2=  s.getReadyQueue();
		   temp2.remove(process); ////come back later
		   s.setReadyQueue(temp2);
		   System.out.println("Ready Queue:");
		   s.printQueue(s.getReadyQueue());
		   System.out.println("Blocked Queue:");
		   s.printQueue(s.getBlockedQueue());}
		   return true;
	   }
   }
   public Process semSignal(Process doneProcess,Schedueler s) { //not done yet
	   if(processUsing==doneProcess) {
		   //get el ba3dy hoto gowa
		   if(!blockedQueue.isEmpty()) {
			   Process newWaiting= blockedQueue.poll();
			   Queue<Process> temp2=  s.getBlockedQueue();
			   temp2.remove(newWaiting); ////come back later
			   s.setBlockedQueue(temp2);
			   Queue<Process> temp=  s.getReadyQueue();
			   temp.add(newWaiting); ////come back later
			   s.setReadyQueue(temp);
		   processUsing=newWaiting;
		   return newWaiting;}
		   else {
			   processUsing=null;
			   return null;
		   }
	   }else {
		   return null;
	   }
   }
		  
}
