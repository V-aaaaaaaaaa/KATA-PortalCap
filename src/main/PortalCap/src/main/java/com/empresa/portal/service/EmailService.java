//IMPLEMENTACION A FUTURO
package com.empresa.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    public void sendNotificationEmail(String to, String userName, String title, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(title);
            mailMessage.setText(
                    "Hola " + userName + ",\n\n" +
                            message + "\n\n" +
                            "Accede al portal para más detalles.\n\n" +
                            "Saludos,\n" +
                            "Portal de Capacitaciones");

            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject("Recuperación de contraseña - Portal de Capacitaciones");
            mailMessage.setText(
                    "Hola " + userName + ",\n\n" +
                            "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                            "Tu código de recuperación es: " + resetToken + "\n\n" +
                            "Este código expira en 1 hora.\n\n" +
                            "Si no solicitaste este cambio, ignora este correo.\n\n" +
                            "Saludos,\n" +
                            "Portal de Capacitaciones");

            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Error enviando correo de recuperación: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String userName) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject("¡Bienvenido al Portal de Capacitaciones!");
            mailMessage.setText(
                    "Hola " + userName + ",\n\n" +
                            "¡Bienvenido al Portal de Capacitaciones!\n\n" +
                            "Esta es tu oportunidad de crecer profesionalmente :).\n\n" +
                            "Explora nuestros módulos:\n" +
                            "• Fullstack Development\n" +
                            "• APIs e Integraciones\n" +
                            "• Cloud Computing\n" +
                            "• Data Engineering\n\n" +
                            "¡Comienza tu camino de aprendizaje hoy!\n\n" +
                            "Saludos,\n" +
                            "Portal de Capacitaciones");

            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Error enviando correo de bienvenida: " + e.getMessage());
        }
    }
}