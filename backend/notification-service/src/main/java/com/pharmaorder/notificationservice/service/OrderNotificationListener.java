package com.pharmaorder.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderNotificationListener {

    private final JavaMailSender mailSender;
    private final org.thymeleaf.TemplateEngine templateEngine;

    public OrderNotificationListener(JavaMailSender mailSender, org.thymeleaf.TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @RabbitListener(queues = "order.queue")
    public void handleOrderCreated(Map<String, Object> event) {
        System.out.println("Processing notification for order: " + event.get("orderId"));
        
        try {
            String email = (String) event.get("userEmail");
            String orderId = (String) event.get("orderId");
            
            // Create context for Thymeleaf
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariables(event);

            // Render template
            String htmlContent = templateEngine.process("order-confirmation", context);

            // Send HTML Email
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setFrom("PharmaOrder <no-reply@pharmaorder.com>");
            helper.setTo(email);
            helper.setSubject("Order Confirmed — PharmaOrder #" + orderId);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            System.out.println("Branded confirmation email sent to: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
