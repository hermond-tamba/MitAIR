package main;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MitAir {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//load configuration
		try {
			while (true){
				AppConfigProperties apc = new AppConfigProperties(args[0]);
				EnvListProperties elp = new EnvListProperties(apc.app_envlist);	
				MitAir air = new MitAir(apc,elp);
				if(air.isOKToRun()){
					air.doProcess();
				}else{
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
	public MitAir(AppConfigProperties apc,EnvListProperties elp ){
		this.apc = apc;
		this.elp = elp;
	}
	
	public void doProcess(){
		for(int i=0;i<elp.vecECP.size();i++){
			
		}
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
		
		return isNowBetweenDateTime(dt2,dt1);
	}
	
	public long timeToWakeUp(){
		Date now = new Date();
		Date dt2 = dateFromHourMinSec(apc.app_stoptime);
		return dt2.getTime()-now.getTime();
	}

}
