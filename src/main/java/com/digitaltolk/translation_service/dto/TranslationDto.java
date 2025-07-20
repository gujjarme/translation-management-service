package com.digitaltolk.translation_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class TranslationDto {
    @NotBlank
    private String key;

    @NotBlank
    @Size(min = 2, max = 5)
    private String locale;

    @NotBlank
    private String content;

    @NotEmpty
    private Set<String> tags;
    public TranslationDto() {
    }
    public TranslationDto(String key, String locale, String content, Set<String> tags) {
        this.key = key;
        this.locale = locale;
        this.content = content;
        this.tags = tags;
    }
    public TranslationDto(String key, String locale, String content) {
        this.key = key;
        this.locale = locale;
        this.content = content;
    }


    public @NotBlank String getKey() {
        return key;
    }

    public void setKey(@NotBlank String key) {
        this.key = key;
    }

    public @NotBlank @Size(min = 2, max = 5) String getLocale() {
        return locale;
    }

    public void setLocale(@NotBlank @Size(min = 2, max = 5) String locale) {
        this.locale = locale;
    }

    public @NotBlank String getContent() {
        return content;
    }

    public void setContent(@NotBlank String content) {
        this.content = content;
    }

    public @NotEmpty Set<String> getTags() {
        return tags;
    }

    public void setTags(@NotEmpty Set<String> tags) {
        this.tags = tags;
    }
}