package edu.uta.solrmetercloud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.jfree.data.gantt.Task;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.service.impl.QueryServiceSolrJImpl;

public class MultiClientsMain extends QueryServiceSolrJImpl {

	public static void main(String[] args) {

		for (int i = 0; i < Util.clientSizePool.length; i++) {
			executeMutilClients(Util.clientSizePool[i]);
		}

	}

	/* Create multiple clients */
	private static void executeMutilClients(int pNumberOfClients) {
		
		ExecutorService taskExecutor = Executors.newFixedThreadPool(pNumberOfClients);

		for (int i = 0; i < pNumberOfClients; i++) {		
			taskExecutor.execute(new ResearchSolrClient(Integer.toString(i)));
		}
		taskExecutor.shutdown();
		try {
			  taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			  
			  //All clients complete queries!
//			  System.out.println(Util.getArryListThroughput().size() +" : "
//					  + Util.getArryListThroughput().toString());
			  
			  System.out.println("#ofClients = " + pNumberOfClients + 
					  " [Throughput, Latency, AvgQTime]: " +
					  Util.calcAverage(0, Util.getArryListThroughput()) * pNumberOfClients + " , " +
					  Util.calcAverage(0, Util.getArryListLatency()) + " , " +
					  Util.calcAverage(0, Util.getArryListAvgQTime()));
			  
			  Util.getArryListAvgQTime().clear();
			  Util.getArryListLatency().clear();
			  Util.getArryListThroughput().clear();
			  
			} catch (InterruptedException e) {
				System.err.println(e);
			}
	}

}
