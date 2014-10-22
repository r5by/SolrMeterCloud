package edu.uta.solrmetercloud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.exception.QueryException;

public class ResearchSolrClient extends Thread {
	
	private Thread t;
	private String clientName;
	private int numberOfClients;
	private ArrayList<Double> mAvgQTimeArray = new ArrayList<Double>();
	private ArrayList<Double>  mThroughputArray= new ArrayList<Double>();
	private ArrayList<Double>  mLatencyArray = new ArrayList<Double>();

	/**
	 * Create the instance of client with information of its ID (clientName) and 
	 * the environment (number of clients)
	 * @param pClientName
	 * @param pNumberOfClients
	 */
	public ResearchSolrClient(String pClientName, int pNumberOfClients) {
		clientName = pClientName;
		numberOfClients = pNumberOfClients;
	}
	
	@Override
	public void run() {
//		System.out.println("Client-" + clientName + " start!");
		int queriesPerClient = Util.TOTAL_QUERIES/numberOfClients;

		try {
			MultiClientsMain mc = new MultiClientsMain();
	
			long sumOfClientTime = 0;
			long sumOfQTime = 0;
			long firstRecordStartTime = 0;
			long lastRecordEndTime = 0;
			int numberOfFinishedRecords = 0;
//			long sectionStartTime = System.currentTimeMillis();
//			long sectionEndTime = sectionStartTime + Util.SECTIONTIME;
			
			for (int i = 0; i < queriesPerClient; i++) {
				long startTime = System.currentTimeMillis();
				
				if(i==1) {
					firstRecordStartTime = startTime;
				}
				
				/* Sending mixed queries to solrcloud */
//				QueryResponse response = mc.executeQuery(Util.qWordGen(), null, null, false, null, null, null, 10, 1, null);
				QueryResponse response = mc.executeQuery(Util.QWORD_1, null, null, false, null, null, null, 10, 1, null);
				//				view.showResults(response);
				
				long endTime = System.currentTimeMillis();

//				System.out.println("Query Time: " + response.getQTime());
//				System.out.println("Client Time: " + (endTime - startTime));
				long clientTime = endTime - startTime;
				writeResultsToFile(response.getQTime(), clientTime);
			
				long iterTime = clientTime;
				
				if(Util.ITERVAL - iterTime > 0)
					sleep(Util.ITERVAL - iterTime);
				
				if (response != null) {
					numberOfFinishedRecords++;
					sumOfClientTime += clientTime;
					sumOfQTime += response.getQTime();
				}
				
				
				if(i == queriesPerClient -1 ) {
					double throughput = 0, latency = 0, avgQTime = 0;
					double numberOfRecords = queriesPerClient -1;
					lastRecordEndTime = endTime;
					
					long totalRecordsTime = lastRecordEndTime - firstRecordStartTime;
					
					throughput = numberOfRecords/(totalRecordsTime/1000.0);
					latency =  ((double)sumOfClientTime)/numberOfFinishedRecords;
					avgQTime = ((double)sumOfQTime)/numberOfFinishedRecords;
					
					mAvgQTimeArray.add(avgQTime);
					mThroughputArray.add(throughput);
					mLatencyArray.add(latency);
					
//					System.out.println("Average Throughput Per Second in Previous Time Section: " + throughput);
					writeResultsToFile("Throughput, Latency, AvgQTime: ", throughput, latency, avgQTime);
				}
				
				
//				if (endTime <= sectionEndTime)
//					if (response != null) {
//						cnt++;
//						sumOfClientTime += clientTime;
//						sumOfQTime += response.getQTime();
//					}

//				if (endTime > sectionEndTime){
//					
//					double throughput = 0, latency = 0, avgQTime = 0;
//					
//					if(cnt!=0) {
//						throughput = cnt/(Util.SECTIONTIME/1000.0);
//						latency = ((double)sumOfClientTime)/cnt;
//						avgQTime = ((double)sumOfQTime)/cnt;
//					} 
					
			}
			
//			writeResultsToFile("[SUMMERY] Throughput, Latency, AvgQTime: ", Util.calcAverage(1, mThroughputArray), Util.calcAverage(1, mLatencyArray), Util.calcAverage(1, mAvgQTimeArray));
//			Util.getArryListThroughput().add(Util.calcAverage(1, mThroughputArray));
//			Util.getArryListLatency().add(Util.calcAverage(1, mLatencyArray));
//			Util.getArryListAvgQTime().add(Util.calcAverage(1, mAvgQTimeArray));
			
			Util.getArryListThroughput().add(Util.calcAverage(0, mThroughputArray));
			Util.getArryListLatency().add(Util.calcAverage(0, mLatencyArray));
			Util.getArryListAvgQTime().add(Util.calcAverage(0, mAvgQTimeArray));
			
//			System.out.println("Client-" + clientName + " finished!");
		} catch (QueryException e) {
			System.err.println(e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@Override
	public void start ()
	   {
	      System.out.println("Starting " +  clientName );
	      if (t == null)
	      {
	         t = new Thread (this, clientName);
	         t.start ();
	      }
	   }
	
	// Write out query time and client time per-query to file
	private void writeResultsToFile(int pQTime, long pCTime) throws IOException {

	      File data = new File(prepareOutPutFileName());
	      
	      if(!data.exists()) {
	    	  data.createNewFile();
	      }
	      
	      FileWriter writer = new FileWriter(data.getName(), true);
	      BufferedWriter buf = new BufferedWriter(writer);
	      
	      buf.write(pQTime + " , " + pCTime + "\n");
	      buf.close();

	}
	
	private void writeResultsToFile(String pTitle, double pThroughput, double pLatency, double pAvgQTime) throws IOException {

	      File data = new File(prepareOutPutFileName());
	      
	      if(!data.exists()) {
	    	  data.createNewFile();
	      }
	      
	      FileWriter writer = new FileWriter(data.getName(), true);
	      BufferedWriter buf = new BufferedWriter(writer);
	      
	      buf.write(pTitle + pThroughput +" , " + pLatency + " , " + pAvgQTime + "\n");
	      buf.close();

	}
	
	private String prepareOutPutFileName() {
		return ("client-" + clientName + "-" + numberOfClients + ".data");
	}
	
	
}
