package com.test.automation.utils;

import java.io.File;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import com.test.automation.config.ConfigReader;

/**
 * Envio de e-mail via SMTP (Gmail) usando Jakarta Mail.
 *
 * Remetente, senha de app e destinatários vêm do config.properties (via ConfigReader),
 * nunca escritos direto no código.
 */
public class EmailUtils {

    private EmailUtils() {
    }

    /**
     * Envia um e-mail em HTML com um arquivo anexo.
     *
     * @param subject        assunto do e-mail
     * @param htmlBody       corpo do e-mail em HTML
     * @param attachmentPath caminho do arquivo a anexar (ex.: o relatório do Extent).
     *                       Se o arquivo não existir, o e-mail é enviado sem anexo.
     */
    public static void sendEmail(String subject, String htmlBody, String attachmentPath) {
        final String sender = ConfigReader.get("email.sender");
        final String appPassword = ConfigReader.get("email.appPassword");
        final String recipients = ConfigReader.get("email.recipients");

        if (sender == null || appPassword == null || recipients == null) {
            Log.error("E-mail não enviado: preencha email.sender, email.appPassword e email.recipients no config.properties.");
            return;
        }

        // Configurações do servidor SMTP
        Properties smtp = new Properties();
        smtp.put("mail.smtp.auth", "true");
        smtp.put("mail.smtp.starttls.enable", "true");
        smtp.put("mail.smtp.host", ConfigReader.get("email.smtp.host", "smtp.gmail.com"));
        smtp.put("mail.smtp.port", ConfigReader.get("email.smtp.port", "587"));

        // Sessão autenticada com a senha de app
        Session session = Session.getInstance(smtp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, appPassword);
            }
        });
        session.setDebug(ConfigReader.getBoolean("email.debug", false));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(subject);

            // Um e-mail com anexo é "multipart": uma parte para o texto e outra para cada anexo
            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(htmlBody, "text/html; charset=utf-8");
            multipart.addBodyPart(bodyPart);

            // Só adiciona a parte do anexo se o arquivo existir.
            // (Uma parte vazia é o que causava o erro "No MimeBodyPart content".)
            File attachment = attachmentPath == null ? null : new File(attachmentPath);
            if (attachment != null && attachment.isFile()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(attachment);
                multipart.addBodyPart(attachmentPart);
                Log.info("Anexando ao e-mail: " + attachment.getAbsolutePath());
            } else {
                Log.warn("Anexo não encontrado, e-mail será enviado sem ele: " + attachmentPath);
            }

            message.setContent(multipart);
            Transport.send(message);
            Log.info("E-mail enviado com sucesso para: " + recipients);

        } catch (Exception e) {
            // Falha no e-mail não deve derrubar a execução dos testes, só é registrada no log
            Log.logException("Falha ao enviar o e-mail: " + e.getMessage(), e);
        }
    }
}
