package com.digitaltolk.translation_service.controller;

import com.digitaltolk.translation_service.dao.Translation;
import com.digitaltolk.translation_service.dto.TranslationDto;
import com.digitaltolk.translation_service.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/translations")
@Tag(name = "Translations", description = "Manage Translations")
public class TranslationController {


    private final TranslationService translationService;
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }
    @Operation(summary = "Create a new translation")
    @PostMapping
    public ResponseEntity<Translation> createTranslation(@RequestBody TranslationDto dto) {
        return ResponseEntity.ok(translationService.createTranslation(dto));
    }
    @Operation(summary = "Search translations with optional filters")
    @GetMapping("/search")
    public ResponseEntity<Page<Translation>> searchTranslations(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String tag,
            Pageable pageable) {
        return ResponseEntity.ok(
                translationService.searchTranslations(key, locale, content, tag, pageable)
        );
    }

    @GetMapping("/export")
    @Operation(summary = "Export translations for a given locale")
    public ResponseEntity<Map<String, String>> exportTranslations(
            @RequestParam(defaultValue = "en") String locale) {
        return ResponseEntity.ok(translationService.exportTranslations(locale));
    }
}