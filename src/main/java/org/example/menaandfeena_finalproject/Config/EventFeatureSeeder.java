package org.example.menaandfeena_finalproject.Config;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Model.EventFeature;
import org.example.menaandfeena_finalproject.Repository.EventFeatureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// يزرع الميزات المعرّفة مسبقاً للفعاليات عند إقلاع التطبيق إن لم تكن موجودة.
// الميزات ثابتة ويختار منها المنظّم؛ الأيقونات يتعامل معها الفرونت إند.
@Component
@RequiredArgsConstructor
public class EventFeatureSeeder implements CommandLineRunner {

    private final EventFeatureRepository eventFeatureRepository;

    private static final List<String> DEFAULT_FEATURES = List.of(
            "مناسبة للعائلات",
            "مناسبة للأطفال",
            "فعالية ثقافية",
            "فعالية تعليمية",
            "فعالية رياضية",
            "فعالية فنية",
            "فعالية تطوعية",
            "تعزيز التواصل",
            "تنمية المهارات",
            "خدمة مجتمعية",
            "ضيافة متنوعة",
            "أجواء رمضانية",
            "مفتوحة للجميع"
    );

    @Override
    public void run(String... args) {
        // نضيف فقط الميزات غير الموجودة حتى لا نكرّرها عند كل إقلاع.
        for (String name : DEFAULT_FEATURES) {
            if (!eventFeatureRepository.existsByName(name)) {
                EventFeature feature = new EventFeature();
                feature.setName(name);
                eventFeatureRepository.save(feature);
            }
        }
    }
}
