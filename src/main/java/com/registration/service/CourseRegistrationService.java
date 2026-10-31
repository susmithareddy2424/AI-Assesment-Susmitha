package com.registration.service;

import com.registration.dao.CourseOffering;
import com.registration.dao.Learner;
import java.util.*;

public class CourseRegistrationService {

    private final Map<String, CourseOffering> courses = new HashMap<>();
    private final Map<String, Learner> learners = new HashMap<>();

    public void addCourse(String courseId, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Course capacity cannot be negative");
        }
        courses.put(courseId, new CourseOffering(courseId, capacity));
    }

    public void registerLearner(Learner learner) {
        learners.put(learner.getLearnerId(), learner);
    }

    /** Attempts enrollment after checking course, learner, and prerequisite eligibility. */
    public String enroll(String courseId, String learnerId) {
        CourseOffering course = courses.get(courseId);
        Learner learner = learners.get(learnerId);

        if (course == null || learner == null) {
            return "INVALID_COURSE_OR_LEARNER";
        }

        if (!learner.hasPrerequisite()) {
            return "PREREQUISITE_FAILED";
        }

        if (course.getEnrolledLearners().contains(learnerId)) {
            return "ALREADY_ENROLLED";
        }
        if (course.getWaitlist().contains(learnerId)) {
            return "ALREADY_ON_WAITLIST";
        }

        if (course.getEnrolledLearners().size() < course.getCapacity()) {
            course.getEnrolledLearners().add(learnerId);
            return "ENROLLED";
        } else {
            course.getWaitlist().add(learnerId);
            return "WAITLISTED";
        }
    }

    /** Drops an enrollment or waitlist entry and promotes the earliest eligible learner when a seat opens. */
    public boolean drop(String courseId, String learnerId) {
        CourseOffering course = courses.get(courseId);
        if (course == null) return false;

        boolean removed = course.getEnrolledLearners().remove(learnerId);
        if (!removed) {
            return course.getWaitlist().remove(learnerId);
        }

        promoteNextEligible(course);
        return true;
    }

    private void promoteNextEligible(CourseOffering course) {
        Iterator<String> iterator = course.getWaitlist().iterator();
        while (iterator.hasNext()) {
            String nextId = iterator.next();
            Learner waitlistedLearner = learners.get(nextId);

            if (waitlistedLearner != null && waitlistedLearner.hasPrerequisite()) {
                iterator.remove();
                course.getEnrolledLearners().add(nextId);
                break; 
            }
        }
    }
    
    public CourseOffering getCourse(String courseId) {
        return courses.get(courseId);
    }
}