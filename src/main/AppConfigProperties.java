package main;

import java.io.FileInputStream;
import java.util.Properties;

public class AppConfigProperties {

	public String email_to;
	public String email_from;
	public String email_host;
	public String email_template;
	public String email_subject;
	
	public String app_starttime;
	public String app_stoptime;
	public long app_delay;
	public boolean app_isemailrequired;
	public boolean app_run24hrs;
	public String app_envlist;
	
	
	public AppConfigProperties(String fname) throws Exception{
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream(fname);
		properties.loadFromXML(fis);
		
		email_to = properties.getProperty("email_to");
		email_from = properties.getProperty("email_from");
		email_host = properties.getProperty("email_host");
		email_template = properties.getProperty("email_template");
		email_subject = properties.getProperty("email_subject");
		app_starttime = properties.getProperty("app_starttime");
		app_stoptime = properties.getProperty("app_stoptime");
		app_delay = Long.valueOf(properties.getProperty("app_delay"));
		app_isemailrequired = Boolean.parseBoolean(properties.getProperty("app_isemailrequired"));
		app_run24hrs = Boolean.parseBoolean(properties.getProperty("app_run24hrs"));
		app_envlist = properties.getProperty("app_envlist");
		
	}
}
