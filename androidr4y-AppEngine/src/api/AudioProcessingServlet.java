package api;
 
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
 
@SuppressWarnings("serial")
public class AudioProcessingServlet extends HttpServlet {
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
 
		try {
			String ak = req.getParameter("ak");
			String tsk = req.getParameter("tsk");
			
			double scale = 0;
			
			if (tsk.equalsIgnoreCase("volup")) {
				scale = 1.1;
			}
			if (tsk.equalsIgnoreCase("voldn")) {
				scale = 0.6;
			}
			
			Key akey = KeyFactory.stringToKey(ak);

			Entity aentity = Storage.get(akey);

			int length = 0;
			String lenString = null;
			if (aentity.getProperty("length") != null) {
				lenString = aentity.getProperty("length").toString();
				//Integer.parseInt((String) aentity.getProperty("length"));
				length = Integer.parseInt(lenString);
			}
			//System.out.println(length);
			
			if (length > 0) {
							
				BlobKey audioBlobKey = new BlobKey(akey.getName());
				byte[] bytes = blobstoreService.fetchData(audioBlobKey, 0, length - 1);
				
				
				// TODO audio processing code.
				byte[] newBytes = new byte[(int) length];	        
				for (int i = 0; i < length; i++) {
					newBytes[i] = bytes[i];
				}
				
				
				short maxVolume = 0;			
				for (int i = 44; i < length; i += 2) {
				    byte[] arr = new byte[2];
				    for (int j = 0; j < 2; j++) {
				    	arr[j] = newBytes[i + j];
				    }
				    ByteBuffer bb = ByteBuffer.wrap(arr);
				    bb.order(ByteOrder.LITTLE_ENDIAN);
				    short volume = bb.getShort();
				    if (Math.abs(volume) > maxVolume)
				    	maxVolume = (short) Math.abs(volume);
				}
				
				System.out.println(maxVolume);
				
				if (scale > 1 && maxVolume * scale < 50000) {
					System.out.println("volume up");
					
					for (int i = 44; i < length; i += 2) {
					    byte[] arr = new byte[2];
					    for (int j = 0; j < 2; j++) {
					    	arr[j] = newBytes[i + j];
					    }
					    ByteBuffer bb = ByteBuffer.wrap(arr);
					    bb.order(ByteOrder.LITTLE_ENDIAN);
					    short volume = bb.getShort();
					    
				    	volume = (short) (volume * scale);
					    
					    //System.out.println(volume);
					    
					    arr[1] = (byte) ((volume >> 8) & 0xFF);
					    arr[0] = (byte) (volume & 0xFF);
					    
					    for (int j = 0; j < 2; j++) {
					    	newBytes[i + j] = arr[j];
					    }
					}
				}
				if (scale < 1) {
					System.out.println("volume down");
					
					for (int i = 44; i < length; i += 2) {
					    byte[] arr = new byte[2];
					    for (int j = 0; j < 2; j++) {
					    	arr[j] = newBytes[i + j];
					    }
					    ByteBuffer bb = ByteBuffer.wrap(arr);
					    bb.order(ByteOrder.LITTLE_ENDIAN);
					    short volume = bb.getShort();
					    
				    	volume = (short) (volume * scale);
					    
					    //System.out.println(volume);
					    
					    arr[1] = (byte) ((volume >> 8) & 0xFF);
					    arr[0] = (byte) (volume & 0xFF);
					    
					    for (int j = 0; j < 2; j++) {
					    	newBytes[i + j] = arr[j];
					    }
					}
				}
		        
		        // write processed audio to a new blob file.
		        FileService fileService = FileServiceFactory.getFileService();
		    	AppEngineFile file = fileService.createNewBlobFile("audio/wav");
		    	boolean lock = true;
		    	FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);	    	
	
		    	writeChannel.write(ByteBuffer.wrap(newBytes));
		    	writeChannel.closeFinally();	    	
		    	BlobKey newBlobKey = fileService.getBlobKey(file);
		        
		    	Entity audioEntity = new Entity("AudioFile", newBlobKey.getKeyString(), akey.getParent());
				//the third level UserRoot->TextFile->AudioFile
				audioEntity.setProperty("usage", aentity.getProperty("usage"));
				audioEntity.setProperty("uploader", aentity.getProperty("uploader"));
				audioEntity.setProperty("time", new Date());
				audioEntity.setProperty("length", lenString);
				audioEntity.setUnindexedProperty("processing", "0");
				Storage.put(audioEntity);
				
				Storage.delete(akey);
				blobstoreService.delete(audioBlobKey);
				
		        //System.out.println("Task complete!");
			}
			
	        Thread.sleep(10000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
 
}

