package hei.school;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerNotificationTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SmsSender smsSender;

    private SubmissionController controller;

    @BeforeEach
    void setUp() {
        Map<String, Object> row = new HashMap<>();
        row.put("id", 42L);
        row.put("title", "Tri à bulles");
        row.put("deadline", Date.valueOf(LocalDate.of(2026, 11, 10)));
        row.put("max_points", new BigDecimal("20"));
        row.put("submitted_at", Timestamp.valueOf(LocalDateTime.of(2026, 11, 9, 18, 0)));
        row.put("full_name", "Alice");
        row.put("email", "alice@etu.kilasy.test");
        row.put("phone", "+261 34 00 000 00");

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(List.of(row));

        controller = new SubmissionController();
        ReflectionTestUtils.setField(controller, "jdbcTemplate", jdbcTemplate);
        ReflectionTestUtils.setField(controller, "mailSender", mailSender);
        ReflectionTestUtils.setField(controller, "smsSender", smsSender);
    }

    @Test
    void smsFailureDoesNotPreventGradePosting() {
        doThrow(new IllegalStateException("SMS unavailable"))
                .when(smsSender).send(anyString(), anyString());

        ResponseEntity<?> response = controller.grade(42L, Map.of("points", 16));

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void emailFailureDoesNotPreventGradePosting() {
        doThrow(new IllegalStateException("Mailpit unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<?> response = controller.grade(42L, Map.of("points", 16));

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void failingSmsProviderDoesNotPreventGradePosting() {
        ReflectionTestUtils.setField(controller, "smsSender", new FailingSmsSender());

        ResponseEntity<?> response = controller.grade(42L, Map.of("points", 16));

        assertEquals(200, response.getStatusCode().value());
        verify(jdbcTemplate).update(anyString(), any(Object[].class));
    }
}
