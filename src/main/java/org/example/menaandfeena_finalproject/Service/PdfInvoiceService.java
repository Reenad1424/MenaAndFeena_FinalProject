package org.example.menaandfeena_finalproject.Service;


import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentInvoiceDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PdfInvoiceService {
    private final PaymentService paymentService;

    public byte[] generateInvoice(String paymentId) throws Exception {
        PaymentInvoiceDTO invoice = paymentService.getPaymentInvoice(paymentId);
        return generateInvoicePdf(invoice);
    }

    public byte[] generateInvoiceForUser(String paymentId, Integer userId) throws Exception {
        PaymentInvoiceDTO invoice = paymentService.getPaymentInvoiceForUser(paymentId, userId);
        return generateInvoicePdf(invoice);
    }

    public static byte[] generateInvoicePdf(PaymentInvoiceDTO invoice) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
        builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
        builder.defaultTextDirection(PdfRendererBuilder.TextDirection.RTL);
        builder.useFont(() -> {
            try {
                return new ClassPathResource("fonts/NotoNaskhArabic-Regular.ttf").getInputStream();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Noto Naskh Arabic");

        builder.withHtmlContent(buildInvoiceHtml(invoice), null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }

    private static String buildInvoiceHtml(PaymentInvoiceDTO invoice) throws Exception {
        byte[] logoBytes = StreamUtils.copyToByteArray(
                new ClassPathResource("static/images/logo.jpeg").getInputStream()
        );
        String logoSrc = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logoBytes);

        String status = invoice.getPaymentStatus() == null
                ? "لا يوجد"
                : invoice.getPaymentStatus().toUpperCase();

        return """
<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
<meta charset="UTF-8" />
<style>
* {
    font-family: "Noto Naskh Arabic";
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

@page { margin: 0; }

body {
    direction: rtl;
    text-align: right;
    background: #FAE9CD;
    color: #333333;
    font-size: 14px;
    padding: 35px 28px;
}

.wrap {
    width: 720px;
    margin-left: auto;
    margin-right: auto;
}

.head {
    text-align: center;
    margin-bottom: 18px;
}

.logo {
    width: 96px;
    height: 96px;
    border-radius: 22px;
    margin-bottom: 14px;
}

.title {
    color: #2e7d32;
    font-size: 30px;
    font-weight: bold;
    margin-bottom: 8px;
}

.subtitle {
    color: #777777;
    font-size: 15px;
}

.line {
    height: 1px;
    background: #e5dfcf;
    margin: 20px 0;
}

.card {
    background: #fffdf8;
    border: 1px solid #eadfcb;
    border-radius: 18px;
    padding: 30px 34px;
}

.card-title {
    color: #2e7d32;
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 4px;
}

.status-badge {
    display: inline-block;
    margin-bottom: 18px;
    padding: 4px 16px;
    font-size: 13px;
    font-weight: bold;
    color: #2e7d32;
    background: #e8f5e9;
    border-radius: 12px;
}

table.details {
    width: 100%%;
    border-collapse: collapse;
}

table.details td {
    padding: 13px 0;
    border-bottom: 1px solid #f1ece0;
    vertical-align: top;
}

table.details td.label {
    width: 35%%;
    text-align: right;
    color: #2e7d32;
    font-weight: bold;
    padding-left: 14px;
    white-space: nowrap;
}

table.details td.value {
    width: 65%%;
    text-align: right;
    direction: ltr;
    color: #555555;
    word-break: break-word;
}

.thanks {
    color: #777777;
    text-align: center;
    margin-top: 22px;
    font-size: 14px;
}
</style>
</head>
<body>
<div class="wrap">
    <div class="head">
        <img class="logo" src="%s" alt="Mena And Feena" />
        <div class="title">فاتورة دفع فعالية</div>
        <div class="subtitle">شكراً للمساهمة في دعم فعاليات وأنشطة الحي السكني.</div>
    </div>

    <div class="line"></div>

    <div class="card">
        <div class="card-title">تفاصيل الدفع</div>
        <span class="status-badge">%s</span>

        <table class="details">
            <tr><td class="label">تاريخ الفعالية</td><td class="value">%s</td></tr>
            <tr><td class="label">وقت الفعالية</td><td class="value">%s</td></tr>
            <tr><td class="label">مكان الفعالية</td><td class="value">%s</td></tr>
            <tr><td class="label">حالة التذكرة</td><td class="value">%s</td></tr>
            <tr><td class="label">رقم العملية</td><td class="value">%s</td></tr>
            <tr><td class="label">الفعالية</td><td class="value">%s</td></tr>
            <tr><td class="label">المبلغ</td><td class="value">%s</td></tr>
            <tr><td class="label">شركة البطاقة</td><td class="value">%s</td></tr>
            <tr><td class="label">حامل البطاقة</td><td class="value">%s</td></tr>
            <tr><td class="label">التاريخ</td><td class="value">%s</td></tr>
        </table>
    </div>

    <div class="thanks">شكراً لاستخدامك مينا وفينا</div>
</div>
</body>
</html>
""".formatted(
                logoSrc,
                html(status),
                html(invoice.getEventDate()),
                html(invoice.getEventTime()),
                html(invoice.getEventLocation()),
                html(invoice.getTicketStatus()),
                html(invoice.getMoyasarPaymentId()),
                html(invoice.getEventTitle()),
                html(invoice.getAmount()),
                html(invoice.getCardCompany()),
                html(invoice.getCardHolderName()),
                html(invoice.getCreatedAt())
        );
    }

    private static String html(Object value) {
        if (value == null) {
            return "لا يوجد";
        }
        return HtmlUtils.htmlEscape(value.toString(), StandardCharsets.UTF_8.name());
    }
}
