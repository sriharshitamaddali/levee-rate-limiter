package com.levee.repository;


import com.levee.model.LeveeConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeveeConfigRepository extends CrudRepository<LeveeConfig, String> {
    Optional<LeveeConfig> findById(String id);
}
