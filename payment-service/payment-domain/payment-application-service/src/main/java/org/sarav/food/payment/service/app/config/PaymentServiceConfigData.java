package org.sarav.food.payment.service.app.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "payment-service")
@Configuration
@Data
public class PaymentServiceConfigData {

    private String paymentRequestTopicName;
    private String paymentResponseTopicName;

}
