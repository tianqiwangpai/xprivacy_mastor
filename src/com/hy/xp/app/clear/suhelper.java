package com.hy.xp.app.clear;

import java.io.DataOutputStream;

public class suhelper {
	static Process process = null;
	static DataOutputStream os = null;
	
	static {
		try{
			process = Runtime.getRuntime().exec("su"); // «–ªªµΩroot’ ∫≈
			os = new DataOutputStream(process.getOutputStream());
		}catch(Exception e){			
		}
	}
	
	public static void WriteCmd(String command){
		if(os == null){
			try{
				process = Runtime.getRuntime().exec("su"); // «–ªªµΩroot’ ∫≈
				os = new DataOutputStream(process.getOutputStream());
			}catch(Exception e){			
			}
		}
		try{
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		}catch(Exception e){			
		}		
	}
}
