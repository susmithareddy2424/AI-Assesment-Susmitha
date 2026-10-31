package com.registration.service;

import com.registration.dao.Learner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseRegistrationServiceTest {

    @Test
    void enrollRejectsUnknownCourseOrLearner() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);

        assertEquals("INVALID_COURSE_OR_LEARNER", service.enroll("CS999", "learner-1"));

        service.registerLearner(new Learner("learner-1", true));
        assertEquals("INVALID_COURSE_OR_LEARNER", service.enroll("CS999", "learner-1"));
        assertEquals("INVALID_COURSE_OR_LEARNER", service.enroll("CS101", "unknown"));
    }

    @Test
    void enrollRejectsLearnerWithoutPrerequisite() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);
        service.registerLearner(new Learner("learner-1", false));

        assertEquals("PREREQUISITE_FAILED", service.enroll("CS101", "learner-1"));
        assertTrue(service.getCourse("CS101").getEnrolledLearners().isEmpty());
        assertTrue(service.getCourse("CS101").getWaitlist().isEmpty());
    }

    @Test
    void enrollsEligibleLearnerAndPreventsDuplicateEnrollment() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);
        service.registerLearner(new Learner("learner-1", true));

        assertEquals("ENROLLED", service.enroll("CS101", "learner-1"));
        assertEquals("ALREADY_ENROLLED", service.enroll("CS101", "learner-1"));
        assertTrue(service.getCourse("CS101").getEnrolledLearners().contains("learner-1"));
        assertEquals(1, service.getCourse("CS101").getEnrolledLearners().size());
    }

    @Test
    void waitlistsLearnerWhenCourseIsFullAndPreventsDuplicateWaitlisting() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);
        service.registerLearner(new Learner("learner-1", true));
        service.registerLearner(new Learner("learner-2", true));
        service.enroll("CS101", "learner-1");

        assertEquals("WAITLISTED", service.enroll("CS101", "learner-2"));
        assertEquals("ALREADY_ON_WAITLIST", service.enroll("CS101", "learner-2"));
        assertEquals("learner-2", service.getCourse("CS101").getWaitlist().get(0));
        assertEquals(1, service.getCourse("CS101").getWaitlist().size());
    }

    @Test
    void promotesEligibleLearnersInFifoOrder() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);
        service.registerLearner(new Learner("learner-1", true));
        service.registerLearner(new Learner("learner-2", true));
        service.registerLearner(new Learner("learner-3", true));
        service.enroll("CS101", "learner-1");
        service.enroll("CS101", "learner-2");
        service.enroll("CS101", "learner-3");

        assertTrue(service.drop("CS101", "learner-1"));

        assertTrue(service.getCourse("CS101").getEnrolledLearners().contains("learner-2"));
        assertFalse(service.getCourse("CS101").getEnrolledLearners().contains("learner-3"));
        assertEquals(1, service.getCourse("CS101").getEnrolledLearners().size());
        assertEquals("learner-3", service.getCourse("CS101").getWaitlist().get(0));
    }

    @Test
    void retainsAndSkipsIneligibleWaitlistedLearnerUntilEligible() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 1);
        service.registerLearner(new Learner("learner-1", true));
        service.registerLearner(new Learner("learner-2", true));
        service.registerLearner(new Learner("learner-3", true));
        service.enroll("CS101", "learner-1");
        assertEquals("WAITLISTED", service.enroll("CS101", "learner-2"));
        service.registerLearner(new Learner("learner-2", false));
        assertEquals("WAITLISTED", service.enroll("CS101", "learner-3"));

        assertTrue(service.drop("CS101", "learner-1"));

        assertTrue(service.getCourse("CS101").getEnrolledLearners().contains("learner-3"));
        assertFalse(service.getCourse("CS101").getEnrolledLearners().contains("learner-1"));
        assertTrue(service.getCourse("CS101").getWaitlist().contains("learner-2"));
        assertFalse(service.getCourse("CS101").getWaitlist().contains("learner-3"));

        service.registerLearner(new Learner("learner-2", true));
        assertTrue(service.drop("CS101", "learner-3"));
        assertTrue(service.getCourse("CS101").getEnrolledLearners().contains("learner-2"));
        assertTrue(service.getCourse("CS101").getWaitlist().isEmpty());
    }

    @Test
    void dropRemovesWaitlistedLearnerAndReturnsFalseForMissingEnrollment() {
        CourseRegistrationService service = new CourseRegistrationService();
        service.addCourse("CS101", 0);
        service.registerLearner(new Learner("learner-1", true));
        service.enroll("CS101", "learner-1");

        assertTrue(service.drop("CS101", "learner-1"));
        assertFalse(service.drop("CS101", "learner-1"));
        assertFalse(service.drop("unknown-course", "learner-1"));
        assertTrue(service.getCourse("CS101").getWaitlist().isEmpty());
    }

    @Test
    void rejectsNegativeCourseCapacity() {
        CourseRegistrationService service = new CourseRegistrationService();

        assertThrows(IllegalArgumentException.class, () -> service.addCourse("CS101", -1));
    }
}