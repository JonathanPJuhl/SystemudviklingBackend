package utils;

import entities.ResetPasswordDTO;
import entities.User;
import facades.UserFacade;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManagerFactory;

public class MailSystem {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public void resetPW(ResetPasswordDTO recipientAndSecurityAnswer) {

        String recipient = recipientAndSecurityAnswer.getEmail();
        String answerToSecurityQuestion = recipientAndSecurityAnswer.getAnswerToSecurityQuestion();

        UserFacade userF = UserFacade.getUserFacade(EMF);
        User user = userF.findUserByUsername(recipient);

        if (user.verifySecurityAnswer(answerToSecurityQuestion)) {

            // Recipient's email ID needs to be mentioned.
            String to = recipient;

            // Sender's email ID needs to be mentioned
            String from = "jjstocks4mobile@gmail.com";

            // Assuming you are sending email from through gmails smtp
            String host = "smtp.gmail.com";

            // Get system properties
            Properties properties = System.getProperties();

            // Setup mail server
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass username and password
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication("jjstocks4mobile@gmail.com", "Datamatik2");

                }

            });

            // Used to debug SMTP issues
            session.setDebug(true);

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                // Set Subject: header field
                message.setSubject("Reset password");

                // Now set the actual message
               // message.setText("Please go to: localhost:3000/resetPW/"+recipient+" to reset your password");
                message.setContent(
                        "<p>Please click: <p> <a href=\"www.ipwithme.com/resetPW/"+recipient+"\">Here</a><p> to reset your password</p><p>Kind regards, JJStocks</p>",
                        "text/html");

                System.out.println("sending...");
                // Send message
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }

        }
        else{
            System.out.println("Securityquestion answers don't match");
        }
    }
}
