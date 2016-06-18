package org.team4159.ircfms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class FMS {
	public static HashSet<Robot> robots = new HashSet<Robot>();

	public static void main(String[] args) throws IOException {
		System.out.println("FMS started.");
		ServerSocket listener = new ServerSocket(4159);
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class Handler extends Thread {
		private int number;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Robot robot;

		/**
		 * Constructs a handler thread, squirreling away the socket. All the
		 * interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {

				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				out.println("I_G");
				number = Integer.parseInt(in.readLine().replace("I_", ""));// Lazy
																			// way
																			// to
																			// do
																			// the
																			// stuff...
																			// change
																			// later
				synchronized (robots) {
					for (Robot r : robots) {
						if (r.number == number) {
							// Robot already connected?!
							System.out.println("Robot already connected... deleting old one.");
							robots.remove(r);
							break;
						}
					}

				}
				out.println("I_ACK");

				robots.add(robot = new Robot(number, socket, in, out));

				System.out.println("Robot " + robot.number + " connected");

				robot.setState(RobotState.TELEOP);

				while (true) {
					if (in.ready()) {
						String input = in.readLine();
						if (input == null) {
							// System.out.println("null input");
							return;
						}

						// Handle packet
						if (input.indexOf("HB_LUB") != -1) {
							out.println("HB_DUB");
						} else
							System.out.println("Robot " + robot.number + ": " + input); // Print
																						// out
																						// unknown
																						// command
					} else {
						if(!robot.checkHeartbeat())
						{
							return;
						}
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				// Robot disconnected
				if (robot != null) {
					System.out.println("Robot " + robot.number + " disconnected");
					robots.remove(robot);
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	enum RobotState {
		AUTONOMOUS, TELEOP, DISABLED
	};
}
