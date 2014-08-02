package com.gmail.dailyefforts.flowchart.config;

public class Config {
	public static final String TIME_FORMAT = "yyyy-MM-dd_HH.mm.ss";
	public static final String SUFFIX_NAME = ".png";
	public static float density = 1f;
	public static final int DEFAULT_STROKE_WIDTH = Math.round(3 * density);

	private Config() {
	}

	public static final int MOVE = Math.round(50 * density);
	public static final int SHAPE_STROKE_WIDTH = Math.round(35 * density);
	public static final int MINI_WIDTH = Math.round(50 * density);
	public static final int MINI_HEIGHT = Math.round(50 * density);
	public static final int DEFAULT_WIDTH = Math.round(200 * density);
	public static final int DEFAULT_HEIGHT = Math.round(150 * density);
}
