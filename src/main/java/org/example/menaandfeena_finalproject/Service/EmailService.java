package org.example.menaandfeena_finalproject.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.example.menaandfeena_finalproject.DTO.In.ContactRequestDto;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Model.User;


import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // =========================
    // CONTACT US EMAIL
    // =========================

    public void sendContactEmail(
            String to,
            ContactRequestDto dto
    ) {

        String subject = "رسالة جديدة من تواصل معنا - منا وفينا";

        String html = """
                <html dir="rtl" lang="ar">
                <body style="font-family:Arial;text-align:center; background:#f6f8f6; padding:25px; color:#333;">
                
                <div style="background:white;text-align:center; border-radius:16px; padding:25px; border:1px solid #e0e0e0;">
                
                <h2 style="color:#2e7d32;text-align:center;">رسالة جديدة من تواصل معنا</h2>
                
                <p style="color:#666;">
                تم استلام رسالة جديدة من أحد مستخدمي منصة منا وفينا.
                </p>
                
                <div style="background:#eef8ef; padding:15px; border-radius:12px; margin:15px 0;text-align:center;">
                <p><strong>الاسم:</strong> %s</p>
                <p><strong>البريد الإلكتروني:</strong> %s</p>
                </div>
                
                <h3 style="color:#2e7d32;">نص الرسالة</h3>
                
                <div style="background:#fafafa; padding:15px; border-radius:12px; line-height:1.8;text-align:center;">
                %s
                </div>
                
                <p style="color:#999; font-size:12px; margin-top:25px;">
                هذه الرسالة مرسلة تلقائياً من منصة منا وفينا.
                </p>
                
                </div>
                </body>
                </html>
                """.formatted(
                dto.getName(),
                dto.getEmail(),
                dto.getMessage()
        );

        sendHtmlEmail(to, subject, html);
    }

    // =========================
    // MAYOR APPOINTMENT EMAIL
    // =========================

    public void sendMayorAppointmentEmail(
            User mayor,
            MayorProfile mayorProfile,
            Integer votes
    ) {

        String neighborhoodName =
                mayorProfile.getNeighborhood() != null
                        ? mayorProfile.getNeighborhood().getName()
                        : "غير محدد";

        String subject = "تهانينا، تم انتخابك عمدة للحي";

        String html = """
                <html dir="rtl" lang="ar">
                <body style="font-family:Arial; background:#f6f8f6; padding:25px; color:#333;text-align:center;">
                
                <div style="text-align:center;background:white; border-radius:16px; padding:25px; border:1px solid #e0e0e0;">
                
                <h2 style="color:#2e7d32;text-align:center;">تهانينا %s</h2>
                
                <p style="font-size:15px;">
                نبارك لك فوزك في انتخابات عمدة الحي، وتم تعيينك عمدةً لحي🌳🏡 <strong>%s</strong>.
                </p>
                
                <div style="text-align:center;background:#eef8ef; padding:15px; border-radius:12px; margin:20px 0;">
                <p><strong>الحي:</strong> %s</p>
                <p><strong>عدد الأصوات:</strong> %d</p>
                <p><strong>بداية الولاية:</strong> %s</p>
                <p><strong>نهاية الولاية:</strong> %s</p>
                </div>
                
                <h3 style="color:#2e7d32;text-align:center;">مهام العمدة</h3>
                
                <ul style="line-height:2;">
                <li>متابعة البلاغات العاجلة وغير العاجلة داخل الحي.</li>
                <li>الاطلاع على تقارير الذكاء الاصطناعي الخاصة بالحي.</li>
                <li>متابعة مؤشرات رضا السكان وجودة الخدمات.</li>
                <li>دعم المبادرات والفعاليات المجتمعية.</li>
                <li>تمثيل سكان الحي ومتابعة احتياجاتهم.</li>
                </ul>
                
                <h3 style="text-align:center;color:#2e7d32;">التقارير المتاحة لك</h3>
                
                <ul style="line-height:2;">
                <li>التقرير الأسبوعي للبلاغات.</li>
                <li>تقرير تقييم أداء الحي.</li>
                <li>تقرير تحليل رضا السكان.</li>
                </ul>
                
                <div style="text-align:center;background:#fff8e1;padding:15px;border-radius:12px;margin-top:20px;">
                🔑 تم تفعيل صلاحيات العمدة لحسابك.
                
                يرجى تسجيل الخروج ثم تسجيل الدخول مرة أخرى للوصول إلى:
                
                <ul>
                <li>لوحة العمدة</li>
                <li>التقارير الذكية</li>
                <li>إدارة البلاغات</li>
                <li>مؤشرات أداء الحي</li>
                </ul>
                </div>
                
                <p style="color:#999; font-size:12px;">
                منصة منا وفينا
                </p>
                
                </div>
                </body>
                </html>
                """.formatted(
                mayor.getFullName(),
                neighborhoodName,
                neighborhoodName,
                votes,
                mayorProfile.getStartDate(),
                mayorProfile.getEndDate()
        );

        sendHtmlEmail(mayor.getEmail(), subject, html);
    }

    // =========================
    // EMAIL WITH ATTACHMENTS
    // =========================

    public void sendEmailWithAttachments(
            String to,
            String subject,
            String message,
            File... attachments
    ) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            if (message != null && !message.isBlank()) {
                helper.setText(message);
            } else {
                helper.setText("");
            }

            for (File file : attachments) {
                helper.addAttachment(file.getName(), file);
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // =========================
    // PRIVATE HTML EMAIL SENDER
    // =========================

    private void sendHtmlEmail(
            String to,
            String subject,
            String html
    ) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send html email", e);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentBytes, String fileName) {
        try {
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment(fileName, new ByteArrayResource(attachmentBytes));
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ApiException("Could not send email attachment");
        }
    }
}