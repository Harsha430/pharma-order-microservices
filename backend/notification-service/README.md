# Notification Service - Email & SMS Notifications

Asynchronous service for sending transactional emails and SMS notifications based on domain events.

## 📋 Overview

Listens to events from other services and sends appropriate notifications to users and admins via email and SMS.

**Port**: 8086

## 🎯 Key Features

- Event-driven email notifications
- Thymeleaf email templates
- Retry mechanism for failed emails
- Notification preferences management
- Email delivery tracking
- Dead letter queue for failures
- SMS notifications (optional)

## 🛠️ Technology Stack

- Spring Boot 3.2.4
- Spring Mail (JavaMail)
- RabbitMQ
- Thymeleaf (Email templates)
- PostgreSQL (Notification log)

## 🗄️ Database Schema

```sql
CREATE TABLE notification_log (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    status VARCHAR(30) DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    order_updates BOOLEAN DEFAULT TRUE,
    prescription_updates BOOLEAN DEFAULT TRUE,
    promotional_emails BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE
);
```

## 📧 Email Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

## 📨 Event Listeners

```java
@Component
public class NotificationEventListener {
    
    @Autowired
    private EmailService emailService;
    
    @RabbitListener(queues = "user.registered.queue")
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.getEmail(), event.getFirstName());
    }
    
    @RabbitListener(queues = "order.placed.queue")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        emailService.sendOrderConfirmationEmail(event);
    }
    
    @RabbitListener(queues = "order.status.changed.queue")
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        emailService.sendOrderStatusUpdateEmail(event);
    }
    
    @RabbitListener(queues = "prescription.status.changed.queue")
    public void handlePrescriptionStatusChanged(PrescriptionStatusChangedEvent event) {
        emailService.sendPrescriptionStatusEmail(event);
    }
    
    @RabbitListener(queues = "inventory.stock.low.queue")
    public void handleLowStock(LowStockEvent event) {
        emailService.sendLowStockAlertToAdmin(event);
    }
    
    @RabbitListener(queues = "auth.password.reset.queue")
    public void handlePasswordReset(PasswordResetEvent event) {
        emailService.sendPasswordResetEmail(event);
    }
}
```

## 📧 Email Service

```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Autowired
    private NotificationLogRepository logRepository;
    
    public void sendWelcomeEmail(String to, String firstName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Welcome to PharmaOrder!");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@pharmaorder.com");
            
            mailSender.send(message);
            
            logNotification(to, "WELCOME_EMAIL", "SUCCESS");
        } catch (Exception e) {
            logNotification(to, "WELCOME_EMAIL", "FAILED", e.getMessage());
            throw new EmailSendException("Failed to send welcome email", e);
        }
    }
    
    public void sendOrderConfirmationEmail(OrderPlacedEvent event) {
        Context context = new Context();
        context.setVariable("orderId", event.getOrderId());
        context.setVariable("totalAmount", event.getTotalAmount());
        context.setVariable("items", event.getItems());
        
        String htmlContent = templateEngine.process("order-confirmation", context);
        
        sendEmail(
            event.getUserEmail(),
            "Order Confirmation - #" + event.getOrderId(),
            htmlContent,
            "ORDER_CONFIRMATION"
        );
    }
    
    @Retryable(
        value = MailException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    private void sendEmail(String to, String subject, String htmlContent, String type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@pharmaorder.com");
            
            mailSender.send(message);
            logNotification(to, type, "SUCCESS");
        } catch (Exception e) {
            logNotification(to, type, "FAILED", e.getMessage());
            throw new EmailSendException("Failed to send email", e);
        }
    }
}
```

## 📄 Email Templates

### welcome-email.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Welcome to PharmaOrder</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1>Welcome to PharmaOrder!</h1>
        <p>Hi <span th:text="${firstName}">User</span>,</p>
        <p>Thank you for registering with PharmaOrder. We're excited to have you on board!</p>
        <p>You can now:</p>
        <ul>
            <li>Browse our wide range of medicines</li>
            <li>Upload prescriptions for validation</li>
            <li>Place orders and track delivery</li>
            <li>Earn loyalty points on every purchase</li>
        </ul>
        <a href="https://pharmaorder.com" style="display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Start Shopping</a>
        <p>Best regards,<br>The PharmaOrder Team</p>
    </div>
</body>
</html>
```

### order-confirmation.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Order Confirmation</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1>Order Confirmed!</h1>
        <p>Your order has been successfully placed.</p>
        <p><strong>Order ID:</strong> <span th:text="${orderId}">12345</span></p>
        <p><strong>Total Amount:</strong> ₹<span th:text="${totalAmount}">500.00</span></p>
        
        <h2>Order Items:</h2>
        <table style="width: 100%; border-collapse: collapse;">
            <thead>
                <tr style="background-color: #f8f9fa;">
                    <th style="padding: 10px; text-align: left;">Product</th>
                    <th style="padding: 10px; text-align: center;">Qty</th>
                    <th style="padding: 10px; text-align: right;">Price</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="item : ${items}">
                    <td style="padding: 10px;" th:text="${item.productName}">Product Name</td>
                    <td style="padding: 10px; text-align: center;" th:text="${item.quantity}">1</td>
                    <td style="padding: 10px; text-align: right;">₹<span th:text="${item.price}">100.00</span></td>
                </tr>
            </tbody>
        </table>
        
        <p>We'll send you another email when your order ships.</p>
        <a th:href="@{https://pharmaorder.com/orders/{orderId}(orderId=${orderId})}" style="display: inline-block; padding: 10px 20px; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px;">Track Order</a>
    </div>
</body>
</html>
```

## 🔄 Retry Mechanism

```java
@Configuration
@EnableRetry
public class RetryConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000L);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        return retryTemplate;
    }
}
```

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

## 📄 License

MIT License - Part of PharmaOrder Platform
