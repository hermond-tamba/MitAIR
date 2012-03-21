package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.ivy.core.resolve.IvyNode;

public class IvyProperties {
	public String baselineVer;
	public String infrastructureVer;
	
	public IvyProperties(String bv, String iv){
		baselineVer = bv;
		infrastructureVer = iv;
	}
	
	public IvyProperties(String fname) throws Exception{
		baselineVer = "";
		infrastructureVer = "";
		FileInputStream fstream2 = new FileInputStream(fname);
		DataInputStream in2 = new DataInputStream(fstream2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
		String strLine2;
		while ((strLine2 = br2.readLine()) != null)   {
			String[] strTmp3 = strLine2.split("=");
			if(strTmp3[0].trim().equalsIgnoreCase("infrastructure.version")){
				infrastructureVer = strTmp3[1];
			}else{
				baselineVer = strTmp3[1];
			}
				
		}
		in2.close();

	}
	
	public IvyProperties(String ivysettingfname, String ivyfname, String ivymodule) throws Exception{
		baselineVer = "";
		infrastructureVer = "";		
		IvyMainProc mitair = new IvyMainProc("-settings " + ivysettingfname + " -ivy " + ivyfname);
        List l = mitair.report.getDependencies();
        for(int i=0;i<l.size();i++){
        	IvyNode inIvy = (IvyNode) l.get(i);
        	String[] str1 = inIvy.getModuleRevision().toString().split(";");
        	if(str1[0].trim().endsWith(ivymodule)){
        		infrastructureVer = str1[1].trim();
        	}else{
        		baselineVer = str1[1].trim();
        	}
        
        }

	}
	
	public void doWriteFile(String fname) throws Exception{
        FileWriter fstream3 = new FileWriter(fname);
        BufferedWriter out = new BufferedWriter(fstream3);
        out.write("infrastructure.version="+infrastructureVer + "\n");
        out.write("baseline.version="+baselineVer);
        out.close();

	}
	
}
