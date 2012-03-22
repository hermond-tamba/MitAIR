package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MitAir {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//load configuration
		String emailDetail = "";
		try {
			while (true){
				
				AppConfigProperties apc = new AppConfigProperties(args[0]);
				EnvListProperties elp = new EnvListProperties(apc.app_envlist);	
				MitAir air = new MitAir(apc,elp);
				air.emailDetail = emailDetail;
				if(air.isOKToRun()){
					air.doProcess();
					emailDetail =  air.emailDetail;
					System.out.println("==email detail==\n"+emailDetail+"===");
					System.out.println("Sleeping for " + apc.app_delay + " ms");
					Thread.sleep(apc.app_delay);
				}else{
					if(apc.app_isemailrequired && !emailDetail.trim().equalsIgnoreCase("")){
						air.doSendEmail();
						emailDetail = "";
					}
					System.out.println("Sleeping until" + apc.app_starttime);
					Thread.sleep(air.timeToWakeUp());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private AppConfigProperties apc;
	private EnvListProperties elp;
	public String emailDetail;
	public MitAir(AppConfigProperties apc,EnvListProperties elp ){
		this.apc = apc;
		this.elp = elp;
	}
	
	public void doProcess(){
		boolean isSomethingDownloaded = false;
		DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss");
		for(int i=0;i<elp.vecECP.size();i++){
			EnvConfigProperty ecp = elp.vecECP.get(i);
			if(!ecp.isNeedtoResolve) continue;
			for(int j=0;j<ecp.ivy.size();j++){
				try {
					IvyMainProc imp = new IvyMainProc("-settings " + ecp.ivysettting + " -ivy " + ecp.ivy.get(j));
					isSomethingDownloaded = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//end for j
			if(isSomethingDownloaded){
				emailDetail = emailDetail + ecp.envName + ": \t" + "baseline.version=" + ecp.baselineVersionNew + "\t" + "infrastructure.version=" + ecp.infVersionNew + " (" + formatter.format(new Date()) + ")\n";
			}
			
		}//end for i
		
	}
	
	public Date dateFromHourMinSec(final String hhmmss)
	{
		if (hhmmss.matches("^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$"))
	    {
	        final String[] hms = hhmmss.split(":");
	        final GregorianCalendar gc = new GregorianCalendar();
	        gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hms[0]));
	        gc.set(Calendar.MINUTE, Integer.parseInt(hms[1]));
	        gc.set(Calendar.SECOND, Integer.parseInt(hms[2]));
	        gc.set(Calendar.MILLISECOND, 0);
	        return gc.getTime();
	    }
	    else
	    {
	        throw new IllegalArgumentException(hhmmss + " is not a valid time, expecting HH:MM:SS format");
	    }
	}
	private boolean isNowBetweenDateTime(final Date s, final Date e)
	{
	    final Date now = new Date();
	    return now.after(s) && now.before(e);
	}
	
	public boolean isOKToRun(){
		if(apc.app_run24hrs) return true;
		Date dt1 = dateFromHourMinSec(apc.app_starttime);
		Date dt2 = dateFromHourMinSec(apc.app_stoptime);
		
		if(dt1.getTime()==dt2.getTime()) return true;
		if(dt1.getTime()>dt2.getTime()) return !isNowBetweenDateTime(dt2,dt1);
		
		return isNowBetweenDateTime(dt1,dt2);
	}
	
	public long timeToWakeUp(){
		Date now = new Date();
		Date dt1 = dateFromHourMinSec(apc.app_starttime);
		//Date dt2 = dateFromHourMinSec(apc.app_stoptime);
		
		if(now.before(dt1)) return dt1.getTime()-now.getTime();
		
		Calendar c = Calendar.getInstance();

		c.setTime(dt1);
		c.add(Calendar.DATE, 1);  // number of days to add
		dt1 = c.getTime();
		return dt1.getTime()-now.getTime();
	}
	
	public void doSendEmail() throws Exception{
	      String to = apc.email_to;

	      // Sender's email ID needs to be mentioned
	      String from = apc.email_from;

	      // Assuming you are sending email from localhost
	      String host = apc.email_host;

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.transport.protocol", "smtp");
	      properties.setProperty("mail.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties,null);

	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));


	          
	         // Set Subject: header field
	         message.setSubject(apc.email_subject);



           message.setText(extractEmailTemplate());

	         // Now set the actual message


	         // Send message
	         Transport.send(message);
	         //System.out.println("Sent message successfully....");

	}
	
	private String extractEmailTemplate() throws Exception{
		String allString = "";
		FileInputStream fstream2 = new FileInputStream(apc.email_template);
		DataInputStream in2 = new DataInputStream(fstream2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
		String strLine2;
		while ((strLine2 = br2.readLine()) != null)   {
				if(strLine2.trim().equalsIgnoreCase("====EMAIL DETAIL HERE===")){
					allString = allString + emailDetail;
				}else{
					allString = allString + strLine2 + "\n";
				}
		}
		in2.close();

		return allString;
	}

}
