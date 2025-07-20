package com.digitaltolk.translation_service.service;

import com.digitaltolk.translation_service.dao.Tag;
import com.digitaltolk.translation_service.dao.Translation;
import com.digitaltolk.translation_service.dto.TranslationDto;
import com.digitaltolk.translation_service.repository.TagRepository;
import com.digitaltolk.translation_service.repository.TranslationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TranslationService {


    private final TranslationRepository translationRepository;

    private final TagRepository tagRepository;

    public TranslationService(TranslationRepository translationRepository, TagRepository tagRepository) {
        this.translationRepository = translationRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Translation createTranslation(TranslationDto dto) {
        // 1. Find which tags already exist
        Set<String> existingNames = tagRepository.findExistingNames(dto.getTags());
        Set<String> newTagNames = dto.getTags().stream()
                .filter(name -> !existingNames.contains(name))
                .collect(Collectors.toSet());

        if (!newTagNames.isEmpty()) {
            List<Tag> newTags = newTagNames.stream()
                    .map(Tag::new)
                    .collect(Collectors.toList());
            tagRepository.saveAll(newTags);
        }

        // 4. Fetch all tags in single query
        Set<Tag> tags = new HashSet<>(tagRepository.findByNames(dto.getTags()));

        // 5. Create and save translation
        Translation translation = new Translation();
        translation.setKey(dto.getKey());
        translation.setLocale(dto.getLocale());
        translation.setContent(dto.getContent());
        translation.setTags(tags);

        return translationRepository.save(translation);
    }

    public Page<Translation> searchTranslations(
            String key, String locale, String content, String tag, Pageable pageable) {
        return translationRepository.search(key, locale, content, tag, pageable);
    }

    public Map<String, String> exportTranslations(String locale) {
        return translationRepository.findByLocale(locale).stream()
                .collect(Collectors.toMap(
                        Translation::getKey,
                        Translation::getContent
                ));
    }
}