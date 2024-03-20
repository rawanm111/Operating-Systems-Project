package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class SystemCalls {
private static Mutex file=new Mutex();
private static Mutex userInput=new Mutex();
private static Mutex userOutput=new Mutex();

//A method for each type of system calls
public static  String execute(Process P,Interpreter intr, Schedueler s) throws IOException {
	//get PC
	int PC= getPC(P,intr.getMainMemory());
	int start= P.getMB().get(0);
	//check if pc dakhal ala el proccess el waraha
	if((start+PC)==(P.getMB().get(1))) {
		//if yes terminate and remove from ready queue
		changeState(P,"TERMINATED",intr);
		System.out.println("Ready Queue:");
    	Schedueler.printQueue(Schedueler.getReadyQueue());
    	System.out.println("Blocked Queue:");
    	Schedueler.printQueue(Schedueler.getBlockedQueue());
		//Schedueler.removeFromReady(P);
	}else if(P.getState().equals(ProcessState.BLOCKED)) {
		    String instruction= getInstruction(P,intr.getMainMemory(),PC);
		    System.out.println("Current excuting instruction is: "+instruction);
		   //Cases hasab el instruction hanady anhy helper
		    String instructionDetails[] = instruction.split(" ");
		    semWait(instructionDetails[1],P,intr,s,PC);
		
	}else {
	//	changeState(P,"RUNNING");
	//get from memory instruction at PC
	String instruction= getInstruction(P,intr.getMainMemory(),PC);
	//incrPC(P,intr.getMainMemory(),PC);
	//Cases hasab el instruction hanady anhy helper
	String instructionDetails[] = instruction.split(" ");
	System.out.println("Current excuting instruction is: "+instruction);
	switch(instructionDetails[0]) {
	case "print": print(instructionDetails[1],P,intr);incrPC(P,intr.getMainMemory(),PC); break;
	case "assign":boolean incr=assign(P,new Scanner(System.in),instructionDetails,intr);if(incr)incrPC(P,intr.getMainMemory(),PC); break;
	case "writeFile": writeFile(instructionDetails[1],instructionDetails[2],P,intr);incrPC(P,intr.getMainMemory(),PC); break;
	case "readFile": readFile(instructionDetails[1],P,intr);incrPC(P,intr.getMainMemory(),PC); break;
	case "printFromTo": printFromTo(instructionDetails[1],instructionDetails[2],intr,P);incrPC(P,intr.getMainMemory(),PC); break;
	case "semWait": semWait(instructionDetails[1],P,intr,s,PC); break;
	case "semSignal": semSignal(instructionDetails[1],P,intr,s);incrPC(P,intr.getMainMemory(),PC); break;
	}}
	
return ""+P.getState();}
	

private static void semSignal(String resourceName, Process P,Interpreter intr,Schedueler s) {
	Process pReady = null;
	switch(resourceName) {
	case "userInput":  pReady=userInput.semSignal(P,s);break;
	case "userOutput": pReady=userOutput.semSignal(P,s);break;
	case "file": pReady=file.semSignal(P,s);break;
	}
	//Schedueler.addToReady(pReady);
	//change state of process
	System.out.println("Ready Queue:");
	Schedueler.printQueue(s.getReadyQueue());
	System.out.println("Blocked Queue:");
	Schedueler.printQueue(s.getBlockedQueue());
	if(pReady!=null)
	changeState(pReady,"READY",intr);
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
	oldmemory[start+1]=state;
	Memory newMem= intr.getMainMemory();
	newMem.setMemory(oldmemory);
	intr.setMainMemory(newMem);
}
private static void semWait(String resourceName, Process p,Interpreter intr, Schedueler s,int PC) {
	Boolean blocked = false;
	switch(resourceName) {
	case "userInput":  blocked=userInput.semWait(p,s);break;
	case "userOutput": blocked=userOutput.semWait(p,s);break;
	case "file": blocked=file.semWait(p,s);break;
	}
	if(blocked) {
	
		changeState(p,"BLOCKED",intr);
		}else {
			incrPC(p,intr.getMainMemory(),PC);
		}
	
}
private static void printFromTo(String num1, String num2, Interpreter intr,Process p) {
	ArrayList<Integer> nums= getFromMemVar(num1,num2,intr,p);
	int n1= nums.get(0);
	int n2= nums.get(1);
	for(int i=n1;i<=n2;i++) {
		System.out.println(i);
	}
}
private static ArrayList<Integer> getFromMemVar(String num1, String num2, Interpreter intr, Process p) {
	int n1 = 0;
	int n2 = 0;
	for(int i=p.getMB().get(0);i<=p.getMB().get(1);i++) {
		String curr= intr.getMainMemory().getMemory()[i];
		String[] currSplit= curr.split(" ");
		if(currSplit[0].equals(num1)) {
			n1=Integer.parseInt(currSplit[1]);
		}
		if(currSplit[0].equals(num2)) {
			n2=Integer.parseInt(currSplit[1]);
		}
		
	}
	ArrayList<Integer> nums= new ArrayList<Integer>();
	nums.add(n1);
	nums.add(n2);
	return nums;
	
}


private static String readFile(String path,Process p, Interpreter intr) {
	String pth=getPath(path,p,intr);
	try (BufferedReader reader = new BufferedReader(new FileReader(pth+".txt"))) {
        String line;
        line = reader.readLine();
       
        return line;
       
    } catch (IOException e) {
        e.printStackTrace();
    }
	return path;
	
}
private static void writeFile(String path, String value,Process p, Interpreter intr) throws IOException {
	String pth=getPath(path,p,intr);
	String val=getPath(value,p,intr);
	
	File file = new File(pth+".txt");
	try (FileWriter writer = new FileWriter(pth+".txt")) {
        writer.write(val);
    } catch (IOException e) {
        e.printStackTrace();
    }
	
}



private static String getPath(String path,Process P, Interpreter intr) {
	for(int i=P.getMB().get(0);i<=P.getMB().get(1);i++) {
		String[] ret= ((intr.getMainMemory().getMemory())[i]).split(" ");
		if((ret[0]).equals(path)) {
			
			return ret[1];
		}
		}
	return path;
	}


private static boolean assign(Process P, Scanner scanner,String[] instructionDetails,Interpreter intr) {
	String var= instructionDetails[1];
	String value= instructionDetails[2];
	String store="";
	if(P.getVariables().size()==3) {
		 System.out.print("You cannot initialise more than 3 variables");
		 return true;
	}
	if(P.getTempVariables().isEmpty()) {
		if(value.equals("input")) {
		    System.out.print("Please enter a value: ");
		    store = scanner.nextLine();}
			else if(value.equals("readFile")) {
				 value= readFile(instructionDetails[3],P,intr);
				 store=value;
			}
		ArrayList<String> temp= P.getTempVariables();
		temp.add(store);
		P.setTempVariables(temp);
		return false;
	}else {
	store= P.getTempVariables().get(0);
	ArrayList<String> temp= P.getTempVariables();
	temp.remove(store);
	P.setTempVariables(temp);
	int start= P.getMB().get(0);
	String[] oldmemory=(intr.getMainMemory().getMemory());
	for(int i=4; i<7;i++ ) {
		String curr=oldmemory[i+start]; 
		if(curr.equals("")) {
			oldmemory[i+start]=var+" "+store;
			ArrayList<String>oldV= P.getVariables();
			oldV.add(var+" "+store);
			P.setVariables(oldV);
			break;
		}
		}
	Memory newMem= intr.getMainMemory();
	newMem.setMemory(oldmemory);
	intr.setMainMemory(newMem);
	return true;}
}
private static void print(String var,Process P, Interpreter intr) {
	int start= P.getMB().get(0);
	for(int i=4; i<7;i++ ) {
	String[] oldmemory=(intr.getMainMemory().getMemory());
	String curr=oldmemory[i]; 
	String[] currSplit= curr.split(" ");
	if(currSplit[0].equals(var)) {
		System.out.println(currSplit[1]);
	}
	}
}
private static void incrPC(Process p, Memory mainMemory, int pC) {
	int start= p.getMB().get(0);
	pC++;
	String[] oldmemory=(mainMemory.getMemory());
	oldmemory[start+2]=""+pC;
	mainMemory.setMemory(oldmemory);
	p.setPC(pC);
}
private static String getInstruction(Process p, Memory mainMemory,int PC) {
	int start= p.getMB().get(0);
	String Instruction= (mainMemory.getMemory())[start+PC+1];
	return Instruction;
}
private static int getPC(Process p, Memory mainMemory) {
	int start= p.getMB().get(0);
	String PC= (mainMemory.getMemory())[start+2];
	int PCret= Integer.parseInt(PC);
	return PCret;
}
}
