package com.lemzeeyyy.quizadminapplication;

public class CourseModel {
    private String courseId;
    private String courseName;
    private int difficulty_level;

    public CourseModel(String courseId, String courseName, int difficulty_level) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.difficulty_level = difficulty_level;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getDifficulty_level() {
        return difficulty_level;
    }

    public void setDifficulty_level(int difficulty_level) {
        this.difficulty_level = difficulty_level;
    }
}
