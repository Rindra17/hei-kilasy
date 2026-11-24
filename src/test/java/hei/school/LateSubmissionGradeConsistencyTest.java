package hei.school;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LateSubmissionGradeConsistencyTest {

    private static final LocalDate DEADLINE = LocalDate.of(2026, 11, 10);
    private static final LocalDateTime SUBMITTED_AT = LocalDateTime.of(2026, 11, 11, 2, 0);
    private static final BigDecimal EXPECTED_GRADE = new BigDecimal("14.4");

    @Mock
    private JdbcTemplate jdbcTemplate;

    private GradeReport gradeReport;
    private CsvExport csvExport;

    @BeforeEach
    void setUp() {
        Map<String, Object> student = Map.of(
                "reference", "STD24001",
                "full_name", "Alice",
                "email", "alice@etu.kilasy.test",
                "cohort", "L3-A");

        Map<String, Object> submission = new LinkedHashMap<>();
        submission.put("reference", "STD24001");
        submission.put("full_name", "Alice");
        submission.put("email", "alice@etu.kilasy.test");
        submission.put("cohort", "L3-A");
        submission.put("title", "Tri à bulles");
        submission.put("deadline", Date.valueOf(DEADLINE));
        submission.put("submitted_at", Timestamp.valueOf(SUBMITTED_AT));
        submission.put("submission_id", 42L);
        submission.put("raw_points", new BigDecimal("16.00"));
        submission.put("max_points", new BigDecimal("20.00"));
        submission.put("feedback", "Bon travail.");

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenAnswer(invocation -> {
            String sql = invocation.getArgument(0, String.class);
            if (sql.startsWith("SELECT * FROM student")) {
                return List.of(student);
            }
            if (sql.startsWith("SELECT e.title")) {
                return List.of(submission);
            }
            if (sql.startsWith("SELECT st.reference")) {
                return List.of(submission);
            }
            throw new AssertionError("Unexpected query: " + sql);
        });
        when(jdbcTemplate.queryForList(anyString())).thenReturn(List.of(submission));

        gradeReport = new GradeReport();
        ReflectionTestUtils.setField(gradeReport, "jdbcTemplate", jdbcTemplate);

        csvExport = new CsvExport();
        ReflectionTestUtils.setField(csvExport, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void lateSubmissionHasTheSameGradeInTranscriptGradeReportAndCsvExport() {
        Map<String, Object> report = gradeReport.build(1L);
        Map<String, Object> transcript = transcriptFor(gradeReport).getBody();
        ResponseEntity<String> csvResponse = csvExport.export();

        Map<String, Object> reportLine = exerciseLine(report);
        Map<String, Object> transcriptLine = exerciseLine(transcript);
        String csvLine = csvResponse.getBody().lines().skip(1).findFirst().orElseThrow();

        assertEquals(EXPECTED_GRADE, reportLine.get("grade"));
        assertEquals(reportLine.get("grade"), transcriptLine.get("grade"));
        assertTrue((Boolean) reportLine.get("late"));
        assertEquals(reportLine.get("late"), transcriptLine.get("late"));
        assertEquals("14,4", csvLine.substring(csvLine.lastIndexOf(';') + 1));
        assertTrue(csvLine.contains(";oui;14,4"));
    }

    private ResponseEntity<Map<String, Object>> transcriptFor(GradeReport report) {
        StudentController controller = new StudentController();
        ReflectionTestUtils.setField(controller, "gradeReport", report);
        return (ResponseEntity<Map<String, Object>>) controller.transcript(1L);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> exerciseLine(Map<String, Object> report) {
        return ((List<Map<String, Object>>) report.get("exercises")).get(0);
    }
}
