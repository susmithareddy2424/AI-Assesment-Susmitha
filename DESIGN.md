# Course Waitlist Design

## Boundary and Data

`CourseRegistrationService` owns the enrollment workflow: adding offerings and learners, enrolling, dropping, and promoting from the waitlist. It uses `CourseOffering` and `Learner` from the existing data model. Course and learner records are held in in-memory maps; this is a single-process prototype, not a persistence layer.

Enrollment returns a status string (`ENROLLED`, `WAITLISTED`, or a clear rejection status). Drop returns `true` when an enrollment or waitlist entry was removed and `false` when the course or entry was not found.

## Eligibility and Queue Policy

- `Learner.hasPrerequisite()` is the single prerequisite rule. An ineligible learner cannot enroll or join the waitlist.
- Eligibility is checked again when promoting. If a learner becomes ineligible while waiting, they remain in their queue position and are skipped for that vacancy. The scan continues to the earliest currently eligible learner.
- A skipped learner is reconsidered on a later vacancy. Re-registering the same learner ID replaces its current learner record, allowing the prerequisite result to be updated in this prototype.
- The waitlist is an `ArrayList`, so iteration preserves arrival order. Enrollment uses a `LinkedHashSet` and checks both enrollment and waitlist membership to prevent duplicate records.

## Capacity and Drops

Capacity must be nonnegative. Enrolled count is checked against capacity before adding an enrollment. Dropping an enrolled learner frees a seat and scans the waitlist; dropping a waitlisted learner removes that request without promotion. Unknown courses and repeated or otherwise missing drops return `false` without changing state.

## Persistence, Concurrency, and Tradeoffs

State is in memory and is lost when the process exits. The service and its collections are not thread-safe; calls are expected to be sequential. No locking or database transaction is used because the starter has no concurrent or persistent implementation.

`CourseOffering` currently exposes mutable collection getters, so callers must not modify those collections directly if they want service invariants to hold. Re-adding an existing course ID replaces its offering and resets its state. Null or blank identifiers are not separately validated. These are follow-up hardening opportunities outside this slice.