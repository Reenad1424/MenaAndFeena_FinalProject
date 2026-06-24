package org.example.menaandfeena_finalproject.Service;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
@Service
public class PdfService {

    public File createPdf(String fileName, String html) {

        try {
            File file = new File(fileName);

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();

            builder.useFont(
                    new File("src/main/resources/fonts/NotoNaskhArabic-Regular.ttf"),
                    "Noto Naskh Arabic"
            );

            builder.useUnicodeBidiSplitter(
                    new ICUBidiSplitter.ICUBidiSplitterFactory()
            );

            builder.useUnicodeBidiReorderer(
                    new ICUBidiReorderer()
            );

            builder.defaultTextDirection(
                    PdfRendererBuilder.TextDirection.RTL
            );

            builder.withHtmlContent(html, null);

            FileOutputStream outputStream = new FileOutputStream(file);

            builder.toStream(outputStream);

            builder.run();

            outputStream.close();

            return file;

        } catch (Exception e) {
            throw new RuntimeException("PDF ERROR : " + e.getMessage(), e);
        }
    }
}