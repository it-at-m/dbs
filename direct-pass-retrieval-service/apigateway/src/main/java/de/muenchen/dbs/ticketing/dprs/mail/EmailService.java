package de.muenchen.dbs.ticketing.dprs.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${prs.mail.from}")
    private String mailFrom;

    @Value("${prs.mail.subject}")
    private String mailSubject;

    @Value("${prs.mail.urlBase}")
    private String urlBase;

    private static final String MAIL_TEMPLATE = """
            <!doctype html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html charset=UTF-8" />
            </head>
            <body style="font-family: Helvetica Neue, Helvetica, Arial, Geneva, sans-serif !important; font-size: 14px;">

                <p>Guten Tag,</p>

                <p>
                    Sie haben einen Link zum Passwort für Ihr Anliegen <b>%s</b> angefordert:
                </p>

                <a href="%s"
                   style="display: inline-block; background: #005a9f; color: #ffffff; font-style: normal; font-weight: normal; line-height: 100%%; margin: 0; text-decoration: none; text-transform: none; padding: 10px 20px 10px 20px; mso-padding-alt: 0px; border-radius: 0px;"
                   target="_blank">
                    <div style="display: flex; align-items: center; ">
                        <span>Passwort anzeigen</span>
                        <svg width="24" height="24" style="padding-left: 12px;" viewBox="0 0 100 100">
                            <path fill="white"
                                  d="M83.333 12.575h-25v8.333h14.942L47.054 47.129l5.892 5.892L79.167 26.8v14.942H87.5v-25a4.167 4.167 0 00-4.167-4.167z"></path>
                            <path fill="white"
                                  d="M79.167 79.242H20.834V20.909h20.833v-8.333H20.834a8.333 8.333 0 00-8.333 8.333v58.333a8.333 8.333 0 008.333 8.333h58.333a8.333 8.333 0 008.333-8.333V58.409h-8.333z"></path>
                        </svg>
                    </div>
                </a>

                <p>
                    Der Link ist <b>30 Minuten</b> gültig.
                </p>

                <p>
                    Mit freundlichen Grüßen<br>
                    Landeshauptstadt München
                </p>

                <p><img style="width:180px;" src="data:image/jpeg;base64,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"></p>

                <p>
                    Behördennummer: <a>089 115</a>
                    <br>
                    Nachricht: <a href="https://www.muenchen.de/meta/kontaktformular" >Kontaktformular</a>
                    <br>
                    Website: <a href="http://stadt.muenchen.de">stadt.muenchen.de</a>
                </p>

                <hr>

                <p>Dies ist eine automatisch generierte Nachricht. Bitte antworten Sie nicht auf diese E-Mail.</p>

            </body>
            </html>
            """;

    public void sendPRMessage(String customerMail, String ticketTitle, String resetKey) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setTo(customerMail);

            helper.setFrom(mailFrom);

            helper.setSubject(mailSubject);

            String htmlMsg = MAIL_TEMPLATE.formatted(HtmlUtils.htmlEscape(ticketTitle), urlBase + resetKey);
            helper.setText(htmlMsg, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleMessage(String from, String[] to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }
}
