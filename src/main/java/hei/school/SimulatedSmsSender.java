package hei.school;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "kilasy.sms.provider",
        havingValue = "simulated",
        matchIfMissing = true)
public class SimulatedSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        System.out.println("[SMS] " + phoneNumber + " : " + message);
    }
}
