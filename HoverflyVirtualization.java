package rest.helpers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.SimulationSource;

public class HoverflyVirtualization {
	boolean hoverFlyStarted=false;
	Hoverfly hoverfly1;
	public void StartSimulation(String filename) {
		
		Path path = Paths.get(new File("").getAbsolutePath()+File.separator+"HoverflySimulationJsons"+File.separator +filename+".json");
		System.out.println("path - " + path);
		hoverfly1 = new Hoverfly(HoverflyConfig.localConfigs()
				.asWebServer().proxyLocalHost().proxyPort(8085), HoverflyMode.SIMULATE);
		hoverfly1.start();
	    hoverfly1.setMode(HoverflyMode.SIMULATE);
	    hoverfly1.simulate(SimulationSource.file(path));
	    hoverFlyStarted=true;
	}
	
	
	  public void closeSimulation() { 
		  if (hoverFlyStarted) {
			  hoverfly1.close(); 
		  	hoverFlyStarted=false;
		  }
	  }
	 
	
}
