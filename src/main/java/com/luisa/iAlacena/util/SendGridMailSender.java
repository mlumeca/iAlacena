package com.luisa.iAlacena.util;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridMailSender {

    private static final Logger log = LoggerFactory.getLogger(SendGridMailSender.class);

    @Value("${SENDGRID_API_KEY}")
    private String sendgridApiKey;

    @Async
    public void sendMail(String to, String subject, String message) throws IOException {
        Email from = new Email("meron.camar24@triana.salesianos.edu");
        Email emailTo = new Email(to);
        Content content = new Content("text/plain", message);
        Mail mail = new Mail(from, subject, emailTo, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("SendGrid Response - Status: {}, Body: {}", response.getStatusCode(), response.getBody());
        } catch (IOException ex) {
            log.error("Error sending email to {}: {}", to, ex.getMessage());
            throw ex;
        }
    }
}