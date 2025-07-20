package com.digitaltolk.translation_service.repository;

import com.digitaltolk.translation_service.dao.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    @Query("SELECT t FROM Translation t JOIN t.tags tag WHERE " +
            "(:key IS NULL OR t.key LIKE %:key%) AND " +
            "(:locale IS NULL OR t.locale = :locale) AND " +
            "(:content IS NULL OR t.content LIKE %:content%) AND " +
            "(:tagName IS NULL OR tag.name = :tagName)")
    Page<Translation> search(
            @Param("key") String key,
            @Param("locale") String locale,
            @Param("content") String content,
            @Param("tagName") String tagName,
            Pageable pageable
    );
    List<Translation> findByLocale(String locale);
}