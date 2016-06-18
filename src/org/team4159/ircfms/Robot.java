package org.team4159.ircfms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.team4159.ircfms.FMS.RobotState;

public class Robot {
	public int number;
	private Socket socket; //Socket in here just in case it's needed
    private BufferedReader in;
    private PrintWriter out;
	
	public Robot(int num, Socket soc, BufferedReader in, PrintWriter out) {
		number = num;
		socket = soc;
		this.in = in;
		this.out = out;
	}
	
	 public void setState(Match.MatchState state) throws IOException
	 {
		 switch(state)
		 {
		case AUTONOMOUS:
			setState(RobotState.AUTONOMOUS);
			break;
		case DISABLED:
			setState(RobotState.DISABLED);
			break;
		case TELEOP:
			setState(RobotState.TELEOP);
			break;
		 }
	 }
	
    public void setState(RobotState state) throws IOException
    {
    	out.print("S_S_");
    	switch(state)
    	{
		case AUTONOMOUS:
			out.println("A");
			break;
		case DISABLED:
			out.println("D");
			break;
		case TELEOP:
			out.println("T");
			break;
    	}
    	
    	while (!in.readLine().equals("S_ACK"));
    }
    
    public RobotState getState() throws IOException
    {
    	out.print("S_G");
    	
    	String input;
    	
    	while ((input = in.readLine()) != "S_ACK");
    	
    	switch(input)
    	{
		case "S_A":
			return RobotState.AUTONOMOUS;
		case "S_D":
			return RobotState.DISABLED;
		case "S_T":
			return RobotState.TELEOP;
    	}
		return null;
    }
}