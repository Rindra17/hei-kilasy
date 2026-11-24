# AI Journal — Library

Tool used: Codex, in Neovim

## Change: journal initialization

### Exchange 1 — 15:46

Request: “Create a Journal_IA.md file to record our exchanges.”
Proposal: create this file with a structured format for each change and exchange.
Decision: pending approval.
Reason: respect the rule that no file should be modified without prior validation.
Verification: file has not been created yet.

## Change: late-grade penalty test

### Exchange 2 — 15:59

Request: “Create a test for a grade of 16/20: on time stays 16, two hours late counts as one day and gives 14.4, three days late gives 11.2, and eight days late gives 8.”
Proposal: add a parameterized JUnit test for a `LatePenalty.apply` method. The test uses `rawGrade` as the input grade, applies a 10% penalty per late day, rounds any positive lateness up to at least one day, and caps the total penalty at 50%.
Decision: approved.
Why: the test defines the expected penalty behavior before the penalty implementation is added.
Verification: not run yet. The test is intentionally expected to fail until `LatePenalty` is implemented.

## Change: late penalty implementation and grade display integration

### Exchange 3 — 17:10

Request: “Create the grading implementation and use it in the grade report, CSV export, and submission controller. Keep the raw grade in the database and apply the penalty only to the displayed grade.”
Proposal: add `Grading` as the central display-grade entry point and `LatePenalty` for the penalty calculation. `Grading` treats the complete deadline day as on time, calculates lateness internally, applies a 10% penalty per late day, and caps the penalty at 50%. Integrate it into `GradeReport`, `CsvExport`, and `SubmissionController`.
Decision: approved and implemented.
Why: the original database value remains available for auditing, while every displayed grade follows the same penalty rule.
Verification: tests added. Maven verification was attempted but could not start because of a local `.m2` file-lock error.

## Change: late-submission grade consistency test

### Exchange 4 — 18:00

Request: “Create a new test: add a late submission of an exercise, then check that the grades in the student transcript, grade report, and CSV export have the same result.”
Proposal: add `LateSubmissionGradeConsistencyTest` using a shared mocked late submission. Verify that the transcript and grade report both expose a late flag and a grade of `14.4/20`, and that the CSV export contains the same late status and grade.
Decision: approved and implemented.
Why: the test protects consistency between all user-facing grade representations when a submission is late.
Verification: the targeted test and full Maven test suite passed successfully — 10 tests, 0 failures.

## Summary

- Files modified: `pom.xml`, `src/main/java/hei/school/LatePenalty.java`, `src/main/java/hei/school/Grading.java`, `src/main/java/hei/school/GradeReport.java`, `src/main/java/hei/school/CsvExport.java`, `src/main/java/hei/school/SubmissionController.java`, `src/test/java/hei/school/LatePenaltyTest.java`, `src/test/java/hei/school/GradingTest.java`, `src/test/java/hei/school/LateSubmissionGradeConsistencyTest.java`, `Journal_IA.md`
- Rules established:
  - show every modification before applying it;
  - wait for approval before modifying a file;
  - never modify existing code without explicit permission;
  - keep the journal in English.
