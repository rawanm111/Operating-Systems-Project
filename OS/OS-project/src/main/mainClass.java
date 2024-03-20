package main;


public class mainClass {
	public static void main(String[] args) throws Exception {
		int timeSlice = 2;
		int arrivalTime1 = 0;
		int arrivalTime2 = 1;
		int arrivalTime3 = 4;
		Interpreter ourInterpreter= new Interpreter();
		ourInterpreter.run("Program_1.txt","Program_2.txt","Program_3.txt",arrivalTime1,arrivalTime2,arrivalTime3,timeSlice);
	}
}
