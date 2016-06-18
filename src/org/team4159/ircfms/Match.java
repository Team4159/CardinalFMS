package org.team4159.ircfms;

import java.io.IOException;

public class Match extends Thread {
	public static final int autoTime = 15;
	public static final int disabledTime = 5;
	public static final int teleopTime = 105;

	Robot red1;
	Robot red2;
	Robot blue1;
	Robot blue2;

	public MatchState currentState;
	public long stateStart; // When the state started in milliseconds
	public boolean running = false;

	public Match(Robot red1, Robot red2, Robot blue1, Robot blue2) { // Allow
																		// for
																		// null
																		// for
																		// robot
																		// noshow
		this.red1 = red1;
		this.red2 = red2;
		this.blue1 = blue1;
		this.blue2 = blue2;
	}

	public void run() {
		running = true;

		setState(MatchState.AUTONOMOUS);

		while (running) {
			if (doneWithState()) {
				switch (currentState) {
				case AUTONOMOUS:
					setState(MatchState.DISABLED);
					break;
				case DISABLED:
					setState(MatchState.TELEOP);
					break;
				case TELEOP:
					running = false;
					break;
				}
			} else {
				switch (currentState) {
				case AUTONOMOUS:
					break;
				case DISABLED:
					break;
				case TELEOP:
					break;
				}
			}
		}
	}

	private void setState(MatchState state) {
		currentState = state;
		stateStart = System.currentTimeMillis();
		try {
			red1.setState(state);
			red2.setState(state);
			blue1.setState(state);
			blue2.setState(state);
		} catch (IOException e) {
		}
	}

	private boolean doneWithState() {
		switch (currentState) {
		case AUTONOMOUS:
			return (System.currentTimeMillis() - stateStart) / 1000 > autoTime;
		case DISABLED:
			return (System.currentTimeMillis() - stateStart) / 1000 > disabledTime;
		case TELEOP:
			return (System.currentTimeMillis() - stateStart) / 1000 > teleopTime;
		default:
			return false;
		}
	}

	public enum MatchState {
		AUTONOMOUS, DISABLED, TELEOP
	}
}
