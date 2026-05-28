package com.blognest.services;

import org.thymeleaf.context.Context;

public interface EmailService {

    void sendSimpleEmail(String to, String subject, String body);

    void sendHtmlEmail(String to, String subject, String htmlBody);

    void sendTemplateEmail(String to, String subject, String templateName, Context context);
}