/*
 * Query that will use memcache and breaks into pages
 * @author: Yan Zou
 */

package api;

//import java.io.IOException;
import java.util.List;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
//import com.google.appengine.api.memcache.MemcacheService;
//import com.google.appengine.api.memcache.MemcacheServiceException;
//import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class CachedQuery {
    
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//private MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	private PreparedQuery pq;
	
	//used by list.jsp(TextFile) and read.jsp(AudioFile)
	public CachedQuery(Key rootKey, String className) {
		/*
		String cacheKey = rootKey.getName() + className;
		if (memcache.contains(cacheKey)) {
			System.out.println("CACHE HIT: " + cacheKey);
			pq = (PreparedQuery) memcache.get(cacheKey);
		} else {
			System.out.println("CACHE MISS: " + cacheKey);
			*/
			Query fileQuery = new Query(className);	//TextFile or AudioFile
			fileQuery.setAncestor(rootKey);
			
			pq = datastore.prepare(fileQuery);
			/*
			try {
				memcache.put(cacheKey, pq);
			} catch (MemcacheServiceException e) {
				//do nothing here
			}
		}*/
	}
	
	//used by search.jsp(Filter on properties)
	public CachedQuery(HashMap<String, String> filter) {
		String owner = filter.get("owner");
		String filename = filter.get("filename");
		String category = filter.get("category");
		String req_type = filter.get("req_type");
		
		/******************************************
		 * Search memcache first
		 ******************************************/
		/*
		String cacheKey = filter.toString();
		if (memcache.contains(cacheKey)) {
			System.out.println("CACHE HIT: " + cacheKey);
			pq = (PreparedQuery) memcache.get(cacheKey);
		} else {
			System.out.println("CACHE MISS: " + cacheKey);
		*/
			/******************************************
			 * Query the files according to the search conditions
			 ******************************************/
			
			Query fileQuery = new Query("TextFile");	//query all the text files
			//fileQuery.setAncestor(rootKey);				//under the user root
			if (owner.compareTo("") != 0) {	//if empty, not adding this filter
				fileQuery.addFilter("owner", Query.FilterOperator.EQUAL, owner);
			}
			if (filename.compareTo("") != 0) {	//if empty, not adding this filter
				fileQuery.addFilter("filename", Query.FilterOperator.EQUAL, filename);
			}
			if (category.compareTo("") != 0) {	//if empty, not adding this filter
				fileQuery.addFilter("category", Query.FilterOperator.GREATER_THAN_OR_EQUAL, category);
				fileQuery.addFilter("category", Query.FilterOperator.LESS_THAN, category + (char)255);
			}
			if (req_type.compareTo("") != 0) {	//if empty, not adding this filter
				fileQuery.addFilter("req_type", Query.FilterOperator.EQUAL, req_type);
			}
			
			pq = datastore.prepare(fileQuery);/*
			try {
				memcache.put(cacheKey, pq);
			} catch (MemcacheServiceException e) {
				//do nothing here
			}
		}*/
	}
	
	//get results from offset to limit+offset
	public List<Entity> getList(int limit, int offset) {
		FetchOptions fetchOp = FetchOptions.Builder.withLimit(limit).offset(offset);
		return pq.asList(fetchOp);
	}
	
	//get the number of total results
	public int getCount() {
		return pq.countEntities(FetchOptions.Builder.withDefaults());
	}
}