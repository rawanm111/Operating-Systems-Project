package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Interpreter {
    private static Memory mainMemory;
    private static Memory diskMemory;
    private static Schedueler schedueler;
    private static int processCounter;
    private static ArrayList<Process> allProcesses;
    private static ArrayList<Process> allProcessesSave;
    public Interpreter() {
    	setMainMemory(new Memory());
    	diskMemory=new Memory();
    	schedueler= new Schedueler();
    	processCounter=1;
    	allProcesses= new ArrayList<Process>();
    	allProcessesSave= new ArrayList<Process>();
    	
    	
    }
    public  void run(String Path1,String Path2, String Path3,int arrivalTime1,int arrivalTime2,int arrivalTime3,int timeSlice) throws IOException {
    	//read file
    	System.out.println("Ready Queue:");
    	schedueler.printQueue(schedueler.getReadyQueue());
    	System.out.println("Blocked Queue:");
    	schedueler.printQueue(schedueler.getBlockedQueue());
    	ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Path1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Store each line in the list
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      //Create new process and allocate makan fel memory leeha
    	Process p1= new Process(processCounter,arrivalTime1);
    	p1.setInstructionCounter(lines.size());
    	ArrayList<Object> returnP = mainMemory.write(p1,lines,schedueler,allProcessesSave);
    	System.out.println("Ready Queue:");
    	schedueler.printQueue(schedueler.getReadyQueue());
    	System.out.println("Blocked Queue:");
    	schedueler.printQueue(schedueler.getBlockedQueue());
    	processCounter++;
    	allProcesses.add(p1);
    	allProcessesSave.add(p1);
    	System.out.println("Process "+p1.getID()+" was written into main memory");
    	
    	 lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Path2))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Store each line in the list
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      //Create new process and allocate makan fel memory leeha
    	 Process p2= new Process(processCounter,arrivalTime2);
    	 p2.setInstructionCounter(lines.size());
    	 processCounter++;
    	 returnP = mainMemory.write(p2,lines,schedueler,allProcessesSave);
    	 System.out.println("Ready Queue:");
     	schedueler.printQueue(schedueler.getReadyQueue());
     	System.out.println("Blocked Queue:");
     	schedueler.printQueue(schedueler.getBlockedQueue());
    	 System.out.println("Process "+p2.getID()+" was written into main memory");
    	
    	if(returnP!=null) {
    		 Process wanted= getProcess(allProcesses,returnP.get(0));
    		System.out.println("Process "+wanted.getID()+" was swapped out of main memory and into disk memory");
    		diskMemory.write(wanted,(ArrayList<String>)returnP.get(1),schedueler,allProcessesSave);
    	}
       	 allProcesses.add(p2);
       	 allProcessesSave.add(p2);
    	 lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Path3))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Store each line in the list
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      //Create new process and allocate makan fel memory leeha
    	Process p3= new Process(processCounter,arrivalTime3);
    	p3.setInstructionCounter(lines.size());
    	processCounter++;
    	returnP=mainMemory.write(p3,lines,schedueler,allProcessesSave);
    	System.out.println("Ready Queue:");
    	schedueler.printQueue(schedueler.getReadyQueue());
    	System.out.println("Blocked Queue:");
    	schedueler.printQueue(schedueler.getBlockedQueue());
    	 
    	 System.out.println("Process "+p3.getID()+" was written into main memory");
    	
    	if(returnP!=null) {
    		 Process wanted2= getProcess(allProcesses,returnP.get(0));
    		System.out.println("Process "+wanted2.getID()+" was swapped out of main memory and into disk memory");
    		diskMemory.write(wanted2,(ArrayList<String>) returnP.get(1),schedueler,allProcessesSave);
    	}
    	allProcesses.add(p3);
    	allProcessesSave.add(p3);
    	ArrayList<Object> prc= updateMemoryBounds(allProcessesSave);
    	mainMemory.setMemory((String[])prc.get(0));
    	diskMemory.setMemory((String[])prc.get(1));
    	ArrayList<Process> allP= new ArrayList<Process>();
    	ArrayList<Process> allPS=new ArrayList<Process>();
    	for(int i=0;i<((ArrayList<Process>)prc.get(2)).size();i++) {
    		allP.add(((ArrayList<Process>)prc.get(2)).get(i));
    		allPS.add(((ArrayList<Process>)prc.get(2)).get(i));
    	}
    	allProcesses=allP;
    	allProcessesSave=allPS;
    	execute(allProcesses,timeSlice);
    	
    }
    private ArrayList<Object> updateMemoryBounds(ArrayList<Process> allProcessesSave) {
    	ArrayList<Object> res= new ArrayList<Object>();
        Hashtable<Integer,Integer> occupiedSizePerProcess= new Hashtable<Integer,Integer>();
        for(int j=0; j<allProcessesSave.size();j++) {
			Process curr= allProcessesSave.get(j);
			int size=7+curr.getInstructionCounter();
			occupiedSizePerProcess.put(curr.getID(), size);
		}
    	String[] memoryNew= mainMemory.getMemory();
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
    	String[] diskNew= diskMemory.getMemory();
    	for(int i=0; i<diskNew.length;i++) {
			ArrayList<String> MBnew= new ArrayList<String>();
			ArrayList<Integer> MB= new ArrayList<Integer>();
			Enumeration<Integer> pID= occupiedSizePerProcess.keys();
			 int wantedID = 0;
			 int removeSize = 0;
			//allocate first valid process to swap
			 while (pID.hasMoreElements()) {
				    int pIDcurr=pID.nextElement();
		        	if(diskNew[i]!=null) {
		        		if(diskNew[i].equals(""+pIDcurr)) {
		        			MBnew.add(""+i);
		        			MB.add(i);
		        			int temp= occupiedSizePerProcess.get(pIDcurr)+i-1;
		        			MBnew.add(""+temp);
		        			MB.add(temp);
		        			diskNew[i+3]=MBnew.get(0)+" "+MBnew.get(1);
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
    	res.add(diskNew);
    	res.add(allProcessesSave);
		return res;
	}
    private static Process getProcess(ArrayList<Process> allProcesses, Object object) {
		for(int i=0;i<allProcesses.size();i++) {
			if(allProcesses.get(i).getID()==(int)object){
				return allProcesses.get(i);
			}
		}
		return null;
	}
	public  void execute(ArrayList<Process> allProcesses,int timeSlice) throws IOException {
    	schedueler.execute(allProcesses,timeSlice,this);
    }
	public static Memory getMainMemory() {
		return mainMemory;
	}
	public static void setMainMemory(Memory mainMemorynew) {
		mainMemory = mainMemorynew;
	}
	public static void swapFromDiskToMem(Process current) {
		//loop on disk
		//save process f heta w delete mel disk
		ArrayList<String> data= new ArrayList<String>();
		String[] diskMemoryNew=diskMemory.getMemory();
		ArrayList<Object> returnP1 =diskMemory.shiftAndReturn2(current,allProcessesSave); //returns swapped process and updates pointer
		Process curr= getProcess(allProcessesSave,returnP1.get(0));
		ArrayList<Object> returnP=mainMemory.write(curr,(ArrayList<String>)returnP1.get(1) ,schedueler,allProcessesSave);
		System.out.println("Process "+current.getID()+" was written into main memory");
		Process wanted= getProcess(allProcessesSave,returnP.get(0));
		if(returnP!=null) {
			 System.out.println("Process "+wanted.getID()+" was written into disk memory");
    		diskMemory.write(wanted,(ArrayList<String>)returnP.get(1),schedueler,allProcessesSave);
    	}
	}
	public Memory getDiskMemory() {
		return diskMemory;
	}
	public void setDiskMemory(Memory newMem2) {
		diskMemory=newMem2;
		
	}
	

}
