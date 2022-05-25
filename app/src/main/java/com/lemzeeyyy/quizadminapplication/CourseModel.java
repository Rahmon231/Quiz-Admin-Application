package com.lemzeeyyy.quizadminapplication;

import java.util.List;

public class CourseModel {
    private String courseId;
    private String courseName;
    private int difficulty_level;
    private String diffCounter;


    public CourseModel(String courseId, String courseName, int difficulty_level, String diffCounter) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.difficulty_level = difficulty_level;
        this.diffCounter = diffCounter;
    }

    public String getDiffCounter() {
        return diffCounter;
    }

    public void setDiffCounter(String diffCounter) {
        this.diffCounter = diffCounter;
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
