package api;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Storage
{
	static public void put(Entity _entity)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		datastore.put(_entity);
		memcache.put(_entity.getKey(), _entity);
	}
	
	static public Entity get(Key _key) throws EntityNotFoundException
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity found = null;
		try {
			if (memcache.contains(_key))
			{
				found = (Entity) (memcache.get(_key));
				System.out.println("Hit in Memcache.");
			}
			else
			{
				found = datastore.get(_key);
				memcache.put(_key, found);
				System.out.println("Miss in Memcache.");
			}
			//System.out.println(found.toString());
		} catch (MemcacheServiceException e){	// catch not found
			
		}
		return found;
	}
	
	static public void delete(Key _key)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		datastore.delete(_key);
		memcache.delete(_key);
	}
	
	static public void delete(List<Key> _key_list)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		for (Key k : _key_list)
		{
			datastore.delete(k);
			memcache.delete(k);
		}
	}
}
