package me.picknchew.teachassist;

import java.util.HashMap;
import java.util.Map;

public class CourseNameLookup {
    private static final String NOT_FOUND = "UNKNOWN COURSE";
    private final Map<String, String> courseNames = new HashMap<>();

    public CourseNameLookup() {
        loadNames();
    }

    private void loadNames() {
        String[] courseMapping = TeachAssistApplication.getInstance().getResources().getStringArray(R.array.course_mapping);

        for (String mapping : courseMapping) {
            String[] courseData = mapping.split("\\|");
            courseNames.put(courseData[0], courseData[1]);
        }
    }

    public String lookup(String courseCode) {
        // remove last character from course code: SCH3U1 -> SCH3U
        String courseName = courseNames.get(courseCode.substring(0, courseCode.length() - 1));
        return courseName == null ? NOT_FOUND : courseName;
    }
}
