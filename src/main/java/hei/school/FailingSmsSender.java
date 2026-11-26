package hei.school;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "kilasy.sms.provider", havingValue = "failing")
public class FailingSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        throw new IllegalStateException("Simulated SMS provider failure");
    }
}
