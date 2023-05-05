package rest.helpers;

import org.apache.log4j.Level;

import rest.executors.TestExecutor;

public class CoforgeFormattedException extends Exception {
	public CoforgeFormattedException() {
		TestExecutor.log.LogMessage(Level.ERROR, "There are test failure's......Please refer to Test Report for more details");
	}
}
