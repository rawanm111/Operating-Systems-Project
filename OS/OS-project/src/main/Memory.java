package main;

import java.util.*;

import main.Process;

public class Memory {

    private int availableMemory;
    private String[] memory; 
    public int pointerLast;
    private Hashtable<Integer,Integer> occupiedSizePerProcess= new Hashtable<Integer,Integer>();

    public Memory() {
    	
    	this.availableMemory=40;
    	this.memory= new String[40];
    	this.pointerLast= 0;
    }

	public int getAvailableMemory() {
		return this.availableMemory;
	}
	public void setAvailableMemory(int memorySize) {
		this.availableMemory = memorySize;
	}

	public ArrayList<Object> write(Process p, ArrayList<String> instructions, Schedueler schedueler, ArrayList<Process> allProcessesSave) {
		//get size needed to store process
		int size= 7+p.getInstructionCounter();
		//check if available size
		if(size<=availableMemory) {
			// if available update memory 
			//how to update memory:
			//set process state to ready 
//			p.setState(ProcessState.READY);
//			if(!schedueler.getReadyQueue().contains(p))
//			schedueler.addToReady(p);
			ArrayList<Integer> MB= new ArrayList<Integer>();
			MB.add(pointerLast);
			int temp=pointerLast+size-1;
			MB.add(temp);
			//Update process boundries
			p.setMB(MB);
			//add PCB details into memory
			memory[pointerLast]=""+p.getID();
			pointerLast++;
			memory[pointerLast]=""+p.getState();
			pointerLast++;
			memory[pointerLast]=""+p.getPC();
			pointerLast++;
			memory[pointerLast]=""+p.getMB().get(0)+" "+p.getMB().get(1);
			pointerLast++;
			//add variables
			for(int i=1;i<=3;i++) {
				
				if(p.getVariables().size()>=i)
					memory[pointerLast]= p.getVariables().get(i-1);
				else
					memory[pointerLast]= "";
				pointerLast++;
			}
			//add instructions
			for(int i=0;i<instructions.size();i++) {
				memory[pointerLast]= instructions.get(i);
				pointerLast++;
			}
			occupiedSizePerProcess.put(p.getID(), size);
			availableMemory-=size;
			return null;
		}else {
			// if not available return process to be added into disk and its lines
			ArrayList<Object> returnP =shiftAndReturn(p,allProcessesSave); //returns swapped process and updates pointer
			//how to update memory:
			//set process state to ready 
			//p.setState(ProcessState.NEW);
//			if(!schedueler.getReadyQueue().contains(p))
//			schedueler.addToReady(p);
			ArrayList<Integer> MB= new ArrayList<Integer>();
			MB.add(pointerLast);
			int temp=pointerLast+size-1;
			MB.add(temp);
			//Update process boundries
			p.setMB(MB);
			//add PCB details into memory
			memory[pointerLast]=""+p.getID();
			pointerLast++;
			memory[pointerLast]=""+p.getState();
			pointerLast++;
			memory[pointerLast]=""+p.getPC();
			pointerLast++;
			memory[pointerLast]=""+p.getMB().get(0)+" "+p.getMB().get(1);
			pointerLast++;
			//add variables
            for(int i=1;i<=3;i++) {
				
				if(p.getVariables().size()>=i)
					memory[pointerLast]= p.getVariables().get(i-1);
				else
					memory[pointerLast]= "";
				pointerLast++;
			}
			//add instructions
			for(int i=0;i<instructions.size();i++) {
				memory[pointerLast]= instructions.get(i);
				pointerLast++;
			}
			occupiedSizePerProcess.put(p.getID(), size);
			availableMemory-=size;
		    return returnP; 
		}
		
		
	}


	ArrayList<Object> shiftAndReturn(Process p,ArrayList<Process> allProcessesSave) {
		int size= 7+p.getInstructionCounter();
		//allocate process to be swapped
		//loop ala hashtable of sizes
		 Enumeration<Integer> pID= occupiedSizePerProcess.keys();
		 int wantedID = 0;
		 int removeSize = 0;
		//allocate first valid process to swap
		 while (pID.hasMoreElements()) {
			    int pIDcurr=pID.nextElement();
	        	if((occupiedSizePerProcess.get(pIDcurr)>=size)||((occupiedSizePerProcess.get(pIDcurr)+availableMemory)>=size)) {
	        		if(pIDcurr!=p.getID()) {
	        		wantedID=pIDcurr;
	        		removeSize= occupiedSizePerProcess.get(pIDcurr);}
	        	}
	        }
		//go to process in memory and remove and store its instructions and id
		 ArrayList<String> instructions= new ArrayList<String>();
		 for(int i=0;i<pointerLast;i++) {
			 if(memory[i].equals(""+wantedID)) {
				 for(int j=0;j<removeSize;j++) {
					 if(j>=7) {
						 instructions.add(memory[i+j]);
					 }
					 memory[i+j]=null;
				 }
				 break;
			 }
		 }
		 
		//shift everything
		//update el pointer//new available size
		 int pointerNew=0;
		 int availbleNew=40;
		 String[] memoryNew=new String[40];
		 for(int i=0;i<memory.length;i++) {
			 if(memory[i]!=null) {
				memoryNew[pointerNew]= memory[i];
				pointerNew++;
				availbleNew=availbleNew-1;
			 }
		 }
		//update el pointer//new available size
		 pointerLast=pointerNew;
		 availableMemory=availbleNew;
		 memory=(String[])(updateMemoryBounds(memoryNew,allProcessesSave)).get(0);
		 allProcessesSave= (ArrayList<Process>)(updateMemoryBounds(memoryNew,allProcessesSave)).get(1);
		//return el process ID and instructions
		 ArrayList<Object> returnArr=new  ArrayList<Object>();
		 returnArr.add(wantedID);
		 returnArr.add(instructions);
		 return returnArr;
	}

	

	public String[] getMemory() {
		return memory;
	}

	public void setMemory(String[] memory) {
		this.memory = memory;
	}
	public boolean getElement(String element) {
		for(int i=0;i<pointerLast;i++) {
			if(this.memory[i].equals(element)) {
				return true;
			}
		}
		return false;
	}
	ArrayList<Object> shiftAndReturn2(Process p,ArrayList<Process> allProcessesSave) {
		int size= 7+p.getInstructionCounter();
		//allocate process to be swapped
		//loop ala hashtable of sizes
		 Enumeration<Integer> pID= occupiedSizePerProcess.keys();
		 int wantedID = 0;
		 int removeSize = 0;
		//allocate first valid process to swap
		 while (pID.hasMoreElements()) {
			    int pIDcurr=pID.nextElement();
	        	if((occupiedSizePerProcess.get(pIDcurr)>=size)||((occupiedSizePerProcess.get(pIDcurr)+availableMemory)>=size)) {
	        		if(pIDcurr==p.getID()) {
	        		wantedID=pIDcurr;
	        		removeSize= occupiedSizePerProcess.get(pIDcurr);}
	        	}
	        }
		//go to process in memory and remove and store its instructions and id
		 ArrayList<String> instructions= new ArrayList<String>();
		 for(int i=0;i<pointerLast;i++) {
			 if(memory[i].equals(""+wantedID)) {
				 for(int j=0;j<removeSize;j++) {
					 if(j>=7) {
						 instructions.add(memory[i+j]);
					 }
					 memory[i+j]=null;
				 }
				 break;
			 }
		 }
		 
		//shift everything
		//update el pointer//new available size
		 int pointerNew=0;
		 int availbleNew=40;
		 String[] memoryNew=new String[40];
		 for(int i=0;i<memory.length;i++) {
			 if(memory[i]!=null) {
				memoryNew[pointerNew]= memory[i];
				pointerNew++;
				availbleNew=availbleNew-1;
			 }
		 }
		//update el pointer//new available size
		 pointerLast=pointerNew;
		 availableMemory=availbleNew;
		 memory=(String[])(updateMemoryBounds(memoryNew,allProcessesSave)).get(0);
		 allProcessesSave= (ArrayList<Process>)(updateMemoryBounds(memoryNew,allProcessesSave)).get(1);
		//return el process ID and instructions
		 ArrayList<Object> returnArr=new  ArrayList<Object>();
		 returnArr.add(wantedID);
		 returnArr.add(instructions);
		 return returnArr;
	}
	 private ArrayList<Object> updateMemoryBounds(String [] memoryNew,ArrayList<Process> allProcessesSave) {
	    	ArrayList<Object> res= new ArrayList<Object>();
	        Hashtable<Integer,Integer> occupiedSizePerProcess= new Hashtable<Integer,Integer>();
	        for(int j=0; j<allProcessesSave.size();j++) {
				Process curr= allProcessesSave.get(j);
				int size=7+curr.getInstructionCounter();
				occupiedSizePerProcess.put(curr.getID(), size);
			}
	    	for(int i=0; i<memoryNew.length;i++) {
				ArrayList<String> MBnew= new ArrayList<String>();
				ArrayList<Integer> MB= new ArrayList<Integer>();
				Enumeration<Integer> pID= occupiedSizePerProcess.keys();
				 int wantedID = 0;
				 int removeSize = 0;
				//allocate first valid process to swap
				 while (pID.hasMoreElements()) {
					    int pIDcurr=pID.nextElement();
			        	if(memoryNew[i]!=null) {
			        		if(memoryNew[i].equals(""+pIDcurr)) {
			        			MBnew.add(""+i);
			        			MB.add(i);
			        			int temp= occupiedSizePerProcess.get(pIDcurr)+i-1;
			        			MBnew.add(""+temp);
			        			MB.add(temp);
			        			memoryNew[i+3]=MBnew.get(0)+" "+MBnew.get(1);
			        			for(int j=0; j<allProcessesSave.size();j++) {
			        				Process curr= allProcessesSave.get(j);
			        				if(curr.getID()==pIDcurr) {
			        					allProcessesSave.remove(curr);
			        					curr.setMB(MB);
			        					allProcessesSave.add(curr);
			        				}
			        			}
			        		}
			        	}
			        }
			
			}
	    	res.add(memoryNew);
	    	res.add(allProcessesSave);
	        return res;
	
		}
}
