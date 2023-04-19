package model;

import java.awt.*;

public enum PlayerColor {
	RED,
	GREEN,
	BLUE,
	YELLOW,
	PURPLE,
	BLACK;


	public static Color getBackground(PlayerColor color) {
		switch (color) {
			case RED:
				return Color.RED;
			case GREEN:
				return Color.GREEN;
			case BLUE:
				return Color.BLUE;
			case YELLOW:
				return Color.YELLOW;
			case PURPLE:
				return Color.decode("#6a0dad");
		}
		return Color.BLACK;
	}


	public static Color getForeground(PlayerColor color) {
		switch (color) {
			case RED:
			case BLUE:
			case PURPLE:
				return Color.WHITE;
			case GREEN:
			case YELLOW:
				return Color.BLACK;
		}
		return Color.WHITE;
	}

}
