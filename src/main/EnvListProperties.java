package main;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

public class EnvListProperties {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Vector<EnvConfigProperty> vecECP;
	
	public EnvListProperties(String fname) throws Exception{
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream(fname);
		properties.loadFromXML(fis);
		
		vecECP = new Vector<EnvConfigProperty>();
		EnvConfigProperty ecp = new EnvConfigProperty();
		
		int i = 0;
		ecp.envName = properties.getProperty("i" + i + "_envName");

		while(ecp.envName!=null){
			ecp.tester_ivysettting = properties.getProperty("i" + i + "_tester_ivysettting");
			ecp.tester_ivyproperties = properties.getProperty("i" + i + "_tester_ivyproperties");
			ecp.tester_ivy = properties.getProperty("i" + i + "_tester_ivy");
			ecp.tester_module = properties.getProperty("i" + i + "_tester_module");
			ecp.ivysettting = properties.getProperty("i" + i + "_ivysettting");
			ecp.ivyproperties = properties.getProperty("i" + i + "_ivyproperties");

			try{
				IvyProperties ipOld = new IvyProperties(ecp.ivyproperties);
				IvyProperties ipNew = new IvyProperties(ecp.tester_ivysettting,ecp.tester_ivyproperties);
				ecp.isNeedtoResolve = isNeedToResolve(ipOld,ipNew);


			}catch(Exception e){
				ecp.isNeedtoResolve = true;
			}
			
			int j = 0;
			String ivyfname = properties.getProperty("i" + i + "_ivy_" + j);
			while(ivyfname!=null){
				ecp.ivy.add(ivyfname);
				j = j++;
				ivyfname = properties.getProperty("i" + i + "_ivy_" + j);
			}

			vecECP.add(ecp);			
			i = i++;
			ecp = new EnvConfigProperty();
			ecp.envName = properties.getProperty("i" + i + "_envName");
		}
	}
	
	private boolean isNeedToResolve(IvyProperties oldprop, IvyProperties newprop){
		if(!oldprop.infrastructureVer.equalsIgnoreCase(newprop.infrastructureVer)) return true;
		if(!oldprop.baselineVer.equalsIgnoreCase(oldprop.baselineVer)) return true;
		return false;
	}
}
