# AI Assistance Record

## Prompts and Contributions

1. Prompt: “can you create junit test class for courseRegistrationservice”
   - AI contribution: proposed JUnit 5 coverage for invalid identifiers, prerequisites, enrollment, waitlisting, drops, and promotion.
   - Decision: used the test class structure and scenarios, then reviewed the setup against the service implementation.
   - Files informed: `src/test/java/com/registration/service/CourseRegistrationServiceTest.java`.

2. Prompt: the CourseWaitlist assessment brief, including “Implement enrollment, dropping, and FIFO waitlist promotion for one course offering” and its assumptions, test, design, AI-use, and run-instruction requirements.
   - AI contribution: compared the existing code to the acceptance criteria and identified that the original promotion test expected an ineligible learner on the waitlist even though `enroll` rejects that learner first.
   - Decision: accepted the finding. Chose and documented the policy that eligibility is checked at enrollment and promotion; an already-waitlisted learner who becomes ineligible remains queued but is skipped until eligible. Updated the test to change eligibility after waitlisting and added an explicit FIFO test. Added a negative-capacity guard and removed unrelated citation artifacts from service comments.
   - Files informed: `src/main/java/com/registration/service/CourseRegistrationService.java`, `src/test/java/com/registration/service/CourseRegistrationServiceTest.java`, and `DESIGN.md`.

3. Prompt: “okk do that” in response to the proposed policy alignment, documentation, and verification steps.
   - AI contribution: applied the test and documentation updates and recorded the verification limitation below.
   - Decision: accepted the scoped changes; no persistence, frontend, concurrency, or external integrations were added.

## Verification

- Independently traced the enrollment path: prerequisite rejection happens before waitlist insertion. The revised test first waitlists the learner while eligible, then changes the learner record and verifies skip, retention, and later promotion after eligibility is restored.
- Editor diagnostics reported no errors for the edited service and test files.
- Attempted a direct production-source compile with `javac`; `javac` is also not available on PATH.
- Attempted `mvn -f "C:\Users\jaik8\Downloads\course-registration\pom.xml" test`; execution could not start because `mvn` is not available on PATH. No Maven wrapper is present, so a passing test run is not claimed. Run `mvn test` after installing Maven 3.x and JDK 17 or later.