package main;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		if(app_run24hrs) return true;
		Date dt1 = dateFromHourMinSec(app_starttime);
		Date dt2 = dateFromHourMinSec(app_stoptime);
		
		if(dt1.getTime()==dt2.getTime()) return true;
		if(dt1.getTime()>dt2.getTime()) return !isNowBetweenDateTime(dt2,dt1);
		
		return isNowBetweenDateTime(dt1,dt2);
	}
	
	public long timeToWakeUp(){
		Date now = new Date();
		Date dt1 = dateFromHourMinSec(app_starttime);
		//Date dt2 = dateFromHourMinSec(apc.app_stoptime);
		
		if(now.before(dt1)) return dt1.getTime()-now.getTime();
		
		Calendar c = Calendar.getInstance();

		c.setTime(dt1);
		c.add(Calendar.DATE, 1);  // number of days to add
		dt1 = c.getTime();
		return dt1.getTime()-now.getTime();
	}
}
