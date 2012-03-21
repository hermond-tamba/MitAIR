package main;

import java.util.Vector;

public class EnvConfigProperty {
	public String envName;
	public String tester_ivysettting;
	public String tester_ivyproperties;
	public String tester_ivy;
	public String tester_module;
	
	public String ivysettting;
	public String ivyproperties;
	public Vector<String> ivy;
	public boolean isNeedtoResolve;
	
	public String baselineVersionNew;
	public String baselineVersionOld;
	public String infVersionNew;
	public String infVersionOld;
	
	public  EnvConfigProperty(){
		ivy = new Vector<String>();
	}
}
