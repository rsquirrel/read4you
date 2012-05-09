/*
 * Query that will use memcache and breaks into pages
 * @author: Yan Zou
 */

package api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class CachedQuery {
    
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private Query query;
	
	//used by list.jsp(TextFile) and read.jsp(AudioFile)
	CachedQuery(Key rootKey, String className) {
		
	}
	
	//used by search.jsp(Filter on properties)
	CachedQuery(Map<String, String> filter) {
		String owner = filter.get("owner");
		String filename = filter.get("filename");
		String category = filter.get("category");
		String req_type = filter.get("req_type");
		
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
		
		FetchOptions fetchOp = FetchOptions.Builder.withDefaults();
		List<Entity> results = datastore.prepare(fileQuery).asList(fetchOp);
	}
}