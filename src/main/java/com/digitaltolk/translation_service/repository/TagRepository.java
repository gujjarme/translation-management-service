package com.digitaltolk.translation_service.repository;

import com.digitaltolk.translation_service.dao.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // Batch fetch tags by names
    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    List<Tag> findByNames(@Param("names") Collection<String> names);

    // Check which names don't exist
    @Query("SELECT t.name FROM Tag t WHERE t.name IN :names")
    Set<String> findExistingNames(@Param("names") Collection<String> names);

    @Query("SELECT t FROM Tag t WHERE t.name = :name")
    Optional<Tag> findByName(@Param("name") String name);

    // Bulk insert (only needed if not using saveAll)

}
