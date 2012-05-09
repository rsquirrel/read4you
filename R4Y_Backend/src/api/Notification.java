package api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.Date;

public class Notification
{
	private User user = UserServiceFactory.getUserService().getCurrentUser();
	private static String CCEMAIL = "marksun1988@gmail.com";	// CC' to my email
	private static String CCEMAIL2 = "probas.322@gmail.com";	// CC' to Yan Zou's email

	public Notification()
	{}
	
	public void sendEmail(String link, Date _date, String _to_addr)
	{
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(this.user.getEmail(), 
            				this.user.getNickname() == null? "User" : this.user.getNickname()));
            msg.addRecipient(Message.RecipientType.TO, 
            		new InternetAddress(_to_addr, _to_addr));
            msg.addRecipient(Message.RecipientType.CC, 
            		new InternetAddress(CCEMAIL, "Shuai Sun"));
            
            msg.addRecipient(Message.RecipientType.CC, 
            		new InternetAddress(CCEMAIL2, "Yan Zou"));
            msg.setSubject("Someone posted an audio for your uploaed text. Check it out!");

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    		String htmlBody = "<p>Hi " + _to_addr + "! </p>\n"
    					+ "<p>One of our users, " + user.getNickname() + " has upload an audio file for your text on " 
    					+ dateFormat.format(_date) + ". \n"
    					+ "Please check it out <b><a href=\"" + link + "\">HERE</a></b></p>\n";
    		
    		System.out.println("DEBUG: {" + htmlBody + "}");

            Multipart mp = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);
            
            msg.setContent(mp);
            System.out.println("DEBUG: {" + msg.toString() + "}");
            
            Transport.send(msg);

        } catch (Exception e) {
        	System.out.println("Sending Email failure.");
        	e.printStackTrace();
        }
	}
}
