package com.registration.dao;

import java.util.*;

public class CourseOffering {
    private final String courseId;
    private final int capacity;
    private final Set<String> enrolledLearners = new LinkedHashSet<>();
    private final List<String> waitlist = new ArrayList<>();

    public CourseOffering(String courseId, int capacity) {
        this.courseId = courseId;
        this.capacity = capacity;
    }

    public String getCourseId() { return courseId; }
    public int getCapacity() { return capacity; }
    public Set<String> getEnrolledLearners() { return enrolledLearners; }
    public List<String> getWaitlist() { return waitlist; }
}