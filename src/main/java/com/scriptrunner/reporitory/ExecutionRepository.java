package com.scriptrunner.reporitory;

import com.scriptrunner.model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExecutionRepository extends JpaRepository<Execution, UUID> {
}
