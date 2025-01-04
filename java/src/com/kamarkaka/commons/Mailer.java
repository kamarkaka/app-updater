package com.kamarkaka.commons;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/** utility class to send emails */
public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    private final Session session;
    private final String from;

    /** constructor */
    public Mailer(String from) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", KProperties.getString("mail.smtp.host"));
        props.put("mail.smtp.port", KProperties.getString("mail.smtp.port"));

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    KProperties.getString("mail.smtp.username"),
                    KProperties.getString("mail.smtp.password")
                );
            }
        });

        this.from = from + " <1>";
    }

    /** send a regular email */
    public boolean send(String[] tos, String subject, String body) {
        return send(tos, subject, body, null);
    }

    /** send a regular email with an attachment file */
    public boolean send(String[] tos, String subject, String body, File attachmentFile) {
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));

            for (String to : tos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }

            message.setSubject(subject);

            if (attachmentFile != null) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.setDataHandler(new DataHandler(new FileDataSource(attachmentFile)));
                attachment.setFileName(attachmentFile.getName());

                MimeMultipart multipart = new MimeMultipart();
                MimeBodyPart bodypart = new MimeBodyPart();

                bodypart.setContent(body, "text/html");
                multipart.addBodyPart(bodypart);
                multipart.addBodyPart(attachment);

                message.setContent(multipart);
            } else {
                message.setContent(body, "text/html");
            }

            logger.info("Sending email...");
            Transport.send(message);
            logger.info("Sent successfully!");
            return true;
        } catch (MessagingException ex) {
            logger.error("Failed to send email", ex);
            return false;
        }
    }
}
