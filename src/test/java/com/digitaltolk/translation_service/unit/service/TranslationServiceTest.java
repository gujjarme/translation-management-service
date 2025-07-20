package com.digitaltolk.translation_service.unit.service;

import com.digitaltolk.translation_service.dao.Tag;
import com.digitaltolk.translation_service.dao.Translation;
import com.digitaltolk.translation_service.dto.TranslationDto;
import com.digitaltolk.translation_service.repository.TagRepository;
import com.digitaltolk.translation_service.repository.TranslationRepository;
import com.digitaltolk.translation_service.service.TranslationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock private TranslationRepository translationRepository;
    @Mock private TagRepository tagRepository;

    @InjectMocks private TranslationService translationService;

    @Test
    void createTranslation_shouldCreateNewTagsWhenNoneExist() {
        // Setup
        TranslationDto dto = new TranslationDto("key1", "en", "Hello", Set.of("greeting", "welcome"));

        when(tagRepository.findExistingNames(anySet())).thenReturn(Collections.emptySet());
        when(tagRepository.findByNames(anySet())).thenReturn(Collections.emptyList());

        ArgumentCaptor<List<Tag>> tagsCaptor = ArgumentCaptor.forClass(List.class);

        // Execute
        translationService.createTranslation(dto);

        // Verify
        verify(tagRepository).saveAll(tagsCaptor.capture());
        List<Tag> savedTags = tagsCaptor.getValue();
        assertEquals(2, savedTags.size());
        assertTrue(savedTags.stream().anyMatch(t -> t.getName().equals("greeting")));
        assertTrue(savedTags.stream().anyMatch(t -> t.getName().equals("welcome")));
    }

    @Test
    void createTranslation_shouldOnlyCreateNewTagsWhenSomeExist() {
        // Setup
        TranslationDto dto = new TranslationDto("key1", "en", "Hello", Set.of("greeting", "welcome"));

        when(tagRepository.findExistingNames(anySet())).thenReturn(Set.of("greeting"));
        when(tagRepository.findByNames(anySet())).thenReturn(List.of(new Tag("greeting")));

        ArgumentCaptor<List<Tag>> tagsCaptor = ArgumentCaptor.forClass(List.class);

        // Execute
        translationService.createTranslation(dto);

        // Verify
        verify(tagRepository).saveAll(tagsCaptor.capture());
        List<Tag> savedTags = tagsCaptor.getValue();
        assertEquals(1, savedTags.size());
        assertEquals("welcome", savedTags.get(0).getName());
    }

    @Test
    void createTranslation_shouldNotCreateTagsWhenAllExist() {
        // Setup
        TranslationDto dto = new TranslationDto("key1", "en", "Hello", Set.of("greeting", "welcome"));

        when(tagRepository.findExistingNames(anySet())).thenReturn(Set.of("greeting", "welcome"));
        when(tagRepository.findByNames(anySet())).thenReturn(List.of(
                new Tag("greeting"),
                new Tag("welcome")
        ));

        // Execute
        translationService.createTranslation(dto);

        // Verify
        verify(tagRepository, never()).saveAll(any());
    }

    @Test
    void createTranslation_shouldAssociateAllTagsWithTranslation() {
        // Setup
        TranslationDto dto = new TranslationDto("key1", "en", "Hello", Set.of("greeting", "welcome"));

        when(tagRepository.findExistingNames(anySet())).thenReturn(Set.of("greeting"));
        when(tagRepository.findByNames(anySet())).thenReturn(List.of(
                new Tag("greeting"),
                new Tag("welcome") // Simulate new tag was created
        ));

        Translation savedTranslation = new Translation();
        when(translationRepository.save(any(Translation.class))).thenReturn(savedTranslation);

        // Execute
        Translation result = translationService.createTranslation(dto);

        // Verify
        ArgumentCaptor<Translation> translationCaptor = ArgumentCaptor.forClass(Translation.class);
        verify(translationRepository).save(translationCaptor.capture());

        Translation translation = translationCaptor.getValue();
        assertEquals("key1", translation.getKey());
        assertEquals("en", translation.getLocale());
        assertEquals("Hello", translation.getContent());
        assertEquals(2, translation.getTags().size());
        assertSame(savedTranslation, result);
    }

    @Test
    void searchTranslations_shouldDelegateToRepository() {
        // Setup
        Page<Translation> mockPage = mock(Page.class);
        Pageable pageable = mock(Pageable.class);

        when(translationRepository.search("key", "en", "text", "tag", pageable))
                .thenReturn(mockPage);

        // Execute
        Page<Translation> result = translationService.searchTranslations(
                "key", "en", "text", "tag", pageable);

        // Verify
        assertSame(mockPage, result);
        verify(translationRepository).search("key", "en", "text", "tag", pageable);
    }

    @Test
    void searchTranslations_shouldHandleNullParameters() {
        // Setup
        Page<Translation> mockPage = mock(Page.class);
        Pageable pageable = mock(Pageable.class);

        when(translationRepository.search(null, null, null, null, pageable))
                .thenReturn(mockPage);

        // Execute
        Page<Translation> result = translationService.searchTranslations(
                null, null, null, null, pageable);

        // Verify
        assertSame(mockPage, result);
    }

    @Test
    void exportTranslations_shouldReturnLocaleMappedContent() {
        // Setup
        List<Translation> translations = Arrays.asList(
                new Translation("key1", "en", "Hello"),
                new Translation("key2", "en", "Goodbye")
        );

        when(translationRepository.findByLocale("en")).thenReturn(translations);

        // Execute
        Map<String, String> result = translationService.exportTranslations("en");

        // Verify
        assertEquals(2, result.size());
        assertEquals("Hello", result.get("key1"));
        assertEquals("Goodbye", result.get("key2"));
    }

    @Test
    void exportTranslations_shouldHandleEmptyResults() {
        // Setup
        when(translationRepository.findByLocale("fr")).thenReturn(Collections.emptyList());

        // Execute
        Map<String, String> result = translationService.exportTranslations("fr");

        // Verify
        assertTrue(result.isEmpty());
    }


}