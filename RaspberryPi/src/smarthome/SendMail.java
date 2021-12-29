
package smarthome;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.mail.*;
import javax.mail.internet.*;


public class SendMail {
   static boolean debug = false;
   
    public boolean SendIT(String filename , String title , String Alert) {
       
	String to = "########@gmail.com" ;//reciver
	String from = "sender Email Address";//sender
	String host = "mail.# your mail service # .com" ; // change it to your Email service 
	
	 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");  
        LocalDateTime now = LocalDateTime.now();  
        
	String msgText1 = "*************\n\n" + Alert + "  \n\n*************\n\n" + dtf.format(now);
        
	String subject = "Smart home - " + title;
	
	// create some properties and get the default Session
	Properties props = System.getProperties();
	props.put("mail.smtp.host", host);
	props.put("mail.smtp.socketFactory.port", "587");
	props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
               //Fill in your data here.
               return new PasswordAuthentication("USERNAME/EMAIL ADDRESS ", "**PASSWORD**");//login info
            }
        });
	session.setDebug(debug);
	
	try {
	    // create a message
	    MimeMessage msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    InternetAddress[] address = {new InternetAddress(to)};
	    msg.setRecipients(Message.RecipientType.TO, address);
	    msg.setSubject(subject);

	    // create and fill the first message part
	    MimeBodyPart mbp1 = new MimeBodyPart();
	    mbp1.setText(msgText1);

	    // create the second message part
	    MimeBodyPart mbp2 = new MimeBodyPart();
	    mbp2.attachFile("/home/pi/Desktop/SmartHome/SavedImg/" + filename);// attach the file to the message
           // System.out.println("File name === " + filename );
            MimeBodyPart mbp3 = new MimeBodyPart();
	    mbp3.attachFile("/home/pi/Desktop/SmartHome/log.txt");// attach the log file to the message
            
	    // create the Multipart and add its parts to it
	    Multipart mp = new MimeMultipart();
	    mp.addBodyPart(mbp1);
	    mp.addBodyPart(mbp2);
            mp.addBodyPart(mbp3);
	    // add the Multipart to the message
	    msg.setContent(mp);
	    // set the Date: header
	    msg.setSentDate(new Date());
	    // send the message
	    Transport.send(msg);
            StaticLog.SaveInFile("Email send - " + title + " msg= " + Alert );
	} catch (MessagingException mex) {
	    Exception ex = null;
             System.out.println("MessagingException mex" + mex.getMessage());

	    if ((ex = mex.getNextException()) != null) {
	       return false;
            }
	} catch (IOException ioex) {
             System.out.println("IOException ioex   " + ioex.getMessage());
              return false;
        }
        System.out.println("Email Sent!");
        return true;
    }

}