package com.registration.dao;

public class Learner {
    private final String learnerId;
    private final boolean hasPrerequisite;

    public Learner(String learnerId, boolean hasPrerequisite) {
        this.learnerId = learnerId;
        this.hasPrerequisite = hasPrerequisite;
    }

    public String getLearnerId() { return learnerId; }
    public boolean hasPrerequisite() { return hasPrerequisite; }
}