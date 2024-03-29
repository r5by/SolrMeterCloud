package edu.uta.solrmetercloud;

import java.util.ArrayList;
import java.util.Random;

public class Util {
	
//----------------------------
//	Fields
//----------------------------
	
	/* Fixed number of queries each client can send -- impl 0 */
//	public static final int TOTAL_QUERIES_PER_CLIENT = 1000;
	
	/* Fixed number of queries that all clients can send -- impl 1 */
	/* TODO: Let sending queries follow poisson dist. */
	public static final int TOTAL_QUERIES = 60000;
	
//	private static long startTime, endTime;
	public final static long ITERVAL = 0;
	
//	public static final int SECTIONTIME = 2000;
	
//	public static final int NUMBER_OF_CLIENTS = 5;
	public static final int[] clientSizePool = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
//	public static final int[] clientSizePool = new int[]{1, 50};

	
	
//	public static final int[] clientSizePool = new int[]{2};
	
	/* Used for generating mixed queries */
	public static final String QWORD_1 = "briefly";
	public static final String QWORD_2 = "briefly threatened";
	public static final String QWORD_3 = "briefly threatened formation";
	public static final String QWORD_4 = "briefly threatened formation guitarist";
	
	/* Data structures for calculating throughput, latency and average query time */
	private static ArrayList<Double> arryListThroughput = new ArrayList<Double>();
	private static ArrayList<Double> arryListLatency = new ArrayList<Double>();
	private static ArrayList<Double> arryListAvgQTime = new ArrayList<Double>();

//----------------------------
//	Methods
//----------------------------
	/* Return average double of an arraylist<Double> */
	public static double calcAverage(int startIndex, ArrayList<Double> pArray) {
		double result = 0;
		int cnt = 0;
		
		// Discard the records before startIndex
		for (int i = startIndex; i < pArray.size(); i++) {
//			result+= pArray.get(i);
			
			if(pArray.get(i)!=0) {
				result+=pArray.get(i);
				cnt++;
			}
			
		}
		
//		result/=(pArray.size()-startIndex);
		result/=cnt;
		return result;
	}
	
	/* Generate query word regarding to its frequency */
	public static String qWordGen() {
		
		String result = null; 
		Random random = new Random();
		
		//Return an integer in [0, 100] (101 is exclusive)
		int aRandNum =random.nextInt(101);
		
		if (aRandNum < 23)
			result = QWORD_1;
		else if (aRandNum >= 23 && aRandNum < 84)
			result = QWORD_2;
		else if (aRandNum >= 84 && aRandNum < 98)
			result = QWORD_3;
		else 
			result = QWORD_4;
		
		return result;
	}

//----------------------------
//	Getters & Setters
//----------------------------
	public static synchronized ArrayList<Double> getArryListThroughput() {
		return arryListThroughput;
	}

	public static synchronized void setArryListThroughput(
			ArrayList<Double> arryListThroughput) {
		Util.arryListThroughput = arryListThroughput;
	}

	public static synchronized ArrayList<Double> getArryListLatency() {
		return arryListLatency;
	}

	public static synchronized void setArryListLatency(
			ArrayList<Double> arryListLatency) {
		Util.arryListLatency = arryListLatency;
	}

	public static synchronized ArrayList<Double> getArryListAvgQTime() {
		return arryListAvgQTime;
	}

	public static synchronized void setArryListAvgQTime(
			ArrayList<Double> arryListAvgQTime) {
		Util.arryListAvgQTime = arryListAvgQTime;
	}
	
}
