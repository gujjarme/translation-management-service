package com.digitaltolk.translation_service.factory;

import com.digitaltolk.translation_service.dao.Tag;
import com.digitaltolk.translation_service.dao.Translation;
import com.digitaltolk.translation_service.dao.User;
import com.digitaltolk.translation_service.repository.TagRepository;
import com.digitaltolk.translation_service.repository.TranslationRepository;
import com.digitaltolk.translation_service.service.UserService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Configuration
@Profile("dev")
public class TestDataFactory {

    private static final Logger logger = LoggerFactory.getLogger(TestDataFactory.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner loadTestData(
            TranslationRepository translationRepo,
            TagRepository tagRepo,
            @Value("${app.testdata.record-count:1000}") int recordCount,
            @Value("${app.testdata.batch-size:100}") int batchSize,
            @Value("${app.testdata.tags:mobile,desktop,web}") List<String> tags,
            @Value("${app.testdata.locales:en,fr,es}") List<String> locales
    ) {
        return args -> {
            // Create test user
            createTestUser();

            // Existing test data creation logic
            createTagsAndTranslations(tagRepo, translationRepo, recordCount, batchSize, tags, locales);
        };
    }

    private void createTestUser() {
        String username = "test";
        String password = "12345678";

        try {
            User user = userService.createUser(username, password);
            logger.info("Created test user: {} with password: {}", user.getUsername(), password);
        } catch (IllegalArgumentException e) {
            logger.info("Test user '{}' already exists. Skipping creation.", username);
        }
    }

    private void createTagsAndTranslations(
            TagRepository tagRepo,
            TranslationRepository translationRepo,
            int recordCount,
            int batchSize,
            List<String> tags,
            List<String> locales
    ) {
        logger.debug("===== STARTING TEST DATA LOADING =====");
        logger.info("Loading {} records in batches of {}", recordCount, batchSize);
        logger.debug("Tags: {}", tags);
        logger.debug("Locales: {}", locales);

        // Create tags and store their IDs
        List<Long> tagIds = new ArrayList<>();
        for (String tagName : tags) {
            logger.debug("Processing tag: {}", tagName);

            Tag tag = tagRepo.findByName(tagName).orElseGet(() -> {
                logger.info("Creating new tag: {}", tagName);
                Tag newTag = new Tag(tagName);
                return tagRepo.save(newTag);
            });

            tagIds.add(tag.getId());
            logger.debug("Tag ID for {}: {}", tagName, tag.getId());
        }

        logger.info("Created {} tags", tagIds.size());

        // Batch insert translations
        Random random = new Random();
        int totalSaved = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < recordCount; i += batchSize) {
            List<Translation> batch = new ArrayList<>();
            int end = Math.min(i + batchSize, recordCount);

            logger.debug("Processing batch from {} to {}", i, end-1);

            for (int j = i; j < end; j++) {
                Translation t = new Translation();
                t.setKey("key_" + j);
                t.setContent("Content for key_" + j);
                t.setLocale(locales.get(random.nextInt(locales.size())));

                // Assign 1-3 random tags using proxies
                Set<Tag> assignedTags = new HashSet<>();
                int tagCount = random.nextInt(3) + 1;
                Collections.shuffle(tagIds);

                for (int k = 0; k < tagCount; k++) {
                    Tag tagProxy = entityManager.getReference(Tag.class, tagIds.get(k));
                    assignedTags.add(tagProxy);
                }

                t.setTags(assignedTags);
                batch.add(t);
            }

            translationRepo.saveAll(batch);
            translationRepo.flush();
            entityManager.clear();

            totalSaved += batch.size();
            if (totalSaved % 10000 == 0) {
                logger.info("Saved {} translations so far...", totalSaved);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("===== COMPLETED TEST DATA LOADING =====");
        logger.info("Loaded {} records in {} ms", recordCount, duration);
        logger.info("Average speed: {}/sec",
                (int)(recordCount / (duration / 1000.0)));
    }
}