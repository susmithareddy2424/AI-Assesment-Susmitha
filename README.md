# Course Waitlist

This Maven project contains a bounded in-memory course enrollment and waitlist service.

## Requirements

- JDK 17 or later
- Apache Maven 3.x

## Run Tests

From the project root, run:

```powershell
mvn test
```

To run only the service tests:

```powershell
mvn -Dtest=CourseRegistrationServiceTest test
```

JUnit 5 tests are under `src/test/java/com/registration/service`.

See [DESIGN.md](DESIGN.md) for behavior and assumptions, and [AI_USE.md](AI_USE.md) for the AI assistance and verification record.