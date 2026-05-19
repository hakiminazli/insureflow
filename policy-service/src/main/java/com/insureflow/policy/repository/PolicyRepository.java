package com.insureflow.policy.repository;

import com.insureflow.policy.entity.PlanType;
import com.insureflow.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    boolean existsByPolicyNumber(String policyNumber);

    boolean existsByPlan(PlanType plan);

    Optional<Policy> findByPlan(PlanType plan);
}
