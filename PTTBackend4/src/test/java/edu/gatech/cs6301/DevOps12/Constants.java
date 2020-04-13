package edu.gatech.cs6301.DevOps12;

class Constants {
	private Constants() {
	}

	public static final String BASE_PATH = "/";

	public static final String USERS_PATH = "/users";

	public static final String USERS_ID_PATH = "/users/%s";

	public static final String PROJECTS_PATH = "/projects";

	public static final String PROJECTS_ID_PATH = "/projects/%s";

	public static final String SESSIONS_PATH = "/sessions";

	public static final String SESSIONS_ID_PATH = "/sessions/%s";

	public static final String REPORT_PATH = "/report?from=%s&to=%s";

	public static final String REPORT_PATH_POMODOROS = "&includeCompletedPomodoros=%s";

	public static final String REPORT_PATH_HOURS = "&includeTotalHoursWorkedOnProject=%s";

	public static final String USER_JSON = "{\"firstName\":\"%s\"," + "\"lastName\":\"%s\"," + "\"email\":\"%s\"}";

	public static final String USER_JSON_WITH_ID = "{\"id\": %s, \"firstName\":\"%s\"," + "\"lastName\":\"%s\","
			+ "\"email\":\"%s\"}";

	public static final String PROJECT_JSON = "{\"projectname\":\"%s\"}";

	public static final String PROJECT_JSON_WITH_ID = "{\"id\": %s, \"projectname\":\"%s\"}";

	public static final String SESSIONS_JSON = "{\"startTime\":\"%s\", \"endTime\":\"%s\", \"counter\":\"%s\"}";

	public static final String SESSIONS_JSON_WITH_ID = "{\"id\": %s, \"startTime\":\"%s\", \"endTime\":\"%s\", \"counter\": %s }";

	public static final String SESSIONS_REPORT_JSON = "{\"startingTime\":\"%s\", \"endingTime\":\"%s\", \"hoursWorked\":%s}";

	public static final String REPORT_START_JSON = "{\"sessions\": [";

	public static final String REPORT_END_JSON = "]}";

	public static final String REPORT_END_WITH_OPTIONALS_JSON = "], \"completedPomodoros\": %s, \"totalHoursWorkedOnProject\": %s}";

	public static final String REPORT_END_WITH_POMODOROS_JSON = "], \"completedPomodoros\": %s}";

	public static final String REPORT_END_WITH_HOURS_JSON = "], \"totalHoursWorkedOnProject\": %s}";
}
