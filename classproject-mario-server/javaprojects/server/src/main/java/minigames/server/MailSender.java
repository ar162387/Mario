package minigames.server.email;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

import io.github.cdimascio.dotenv.Dotenv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MailSender extends AbstractVerticle {

        private static final Logger logger = LogManager.getLogger(MailSender.class);


    @Override
    public void start() {

        // Load environment variables from the .env file
        Dotenv dotenv = Dotenv.load();

        String username = dotenv.get("GMAIL_USERNAME");
        String password = dotenv.get("GMAIL_PASSWORD");

        // Configure Vert.x MailClient to use Gmail SMTP server
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHostname("smtp.gmail.com");
        mailConfig.setPort(587); // Use port 587 for TLS
        mailConfig.setStarttls(StartTLSOptions.REQUIRED);
        mailConfig.setUsername(username);
        mailConfig.setPassword(password);
        mailConfig.setAuthMethods("PLAIN");
        MailClient mailClient = MailClient.create(vertx, mailConfig);

        // Register an event bus consumer for sending emails
        vertx.eventBus().consumer("email.send", message -> {
            logger.info("Received an email message for sending: " + message.body());
            // Assume the message body is a JsonObject with email details
            JsonObject emailDetails = (JsonObject) message.body();
            
            MailMessage email = new MailMessage()
                .setFrom(username)
                .setTo(emailDetails.getString("to"))
                .setSubject(emailDetails.getString("subject"))
                .setText(emailDetails.getString("body"));

            // Send the email
            mailClient.sendMail(email, result -> {
                if (result.succeeded()) {
                    logger.info("Email sent successfully!");
                    message.reply("Email sent successfully!");
                } else {
                    logger.error("Failed to send email: " + result.cause().getMessage());
                    message.fail(1, result.cause().getMessage());
                }
            });

    });
    }
}


