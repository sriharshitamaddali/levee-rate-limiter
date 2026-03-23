package com.levee.repository;

import com.levee.model.LeveeUsage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeveeUsageRepository extends CrudRepository<LeveeUsage, String> {
    Optional<LeveeUsage> findById(String id);
}
