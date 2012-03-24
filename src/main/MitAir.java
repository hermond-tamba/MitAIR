package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

				if(apc.isOKToRun()){
					EnvListProperties elp = new EnvListProperties(apc.app_envlist);	
					MitAir air = new MitAir(apc,elp);
					air.emailDetail = emailDetail;					
					air.doProcess();
					emailDetail =  air.emailDetail;
					System.out.println("==email detail==\n"+emailDetail+"===");
					System.out.println("Sleeping for " + apc.app_delay + " ms");
					Thread.sleep(apc.app_delay);
				}else{
					if(apc.app_isemailrequired && !emailDetail.trim().equalsIgnoreCase("")){
						MitAir air = new MitAir(apc,emailDetail);
						air.doSendEmail();
						emailDetail = "";
					}
					System.out.println("Sleeping until" + apc.app_starttime);
					Thread.sleep(apc.timeToWakeUp());
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
	
	public MitAir(AppConfigProperties apc,String emailDetail ){
		this.apc = apc;
		this.emailDetail = emailDetail;
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
