/**
 * Copyright Plugtree LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plugtree.solrmeter.model.operation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.generator.QueryGenerator;

/**
 * Operation that executes a single query
 * @author tflobbe
 *
 */
public class QueryOperation implements Operation {
  
  private Logger logger = Logger.getLogger(this.getClass());
  
  private QueryExecutor executor;
  
  private QueryGenerator queryGenerator;
  
  public QueryOperation(QueryExecutor executor, QueryGenerator queryGenerator) {
    this.executor = executor;
    this.queryGenerator = queryGenerator;
  }
  
  
  public boolean execute() {
    SolrQuery query = queryGenerator.generate();
    try {
      logger.debug("executing query: " + query);
      //
      System.out.println("executing query: " + query);
      long init = System.nanoTime();
      QueryResponse response = this.executeQuery(query);
      long clientTime = (System.nanoTime() - init)/1000000;
      logger.debug(response.getResults().getNumFound() + " results found in " + response.getQTime() + " ms");
      //
      System.out.println(response.getResults().getNumFound() + " results found in " + response.getQTime() + " ms");
      
      /** [RESEARCH] code insertion
       * write out query response time to output for further analysis */
      File data = new File("data-query-res-time.txt");
      
      if(!data.exists()) {
    	  data.createNewFile();
      }
      
      FileWriter writer = new FileWriter(data.getName(), true);
      BufferedWriter buf = new BufferedWriter(writer);
      
      buf.write(response.getQTime() + "\n");
      buf.close();
      
      /** [RESEARCH] - insertion code end */
      
      
      if(response.getQTime() < 0) {
        throw new RuntimeException("The query returned less than 0 as q time: " + response.getResponseHeader().get("q") + response.getQTime());
      }
      
      executor.notifyQueryExecuted(response, clientTime);
    } catch (SolrServerException e) {
      logger.error("Error on Query " + query);
      e.printStackTrace();
      executor.notifyError(new QueryException(e, query));
      return false;
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return true;
  }
  
  protected QueryResponse executeQuery(SolrQuery query) throws SolrServerException {
	String requestMethod = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_METHOD, "GET");
	return executor.getSolrServer().query(query, METHOD.valueOf(requestMethod));
  }
  
}
