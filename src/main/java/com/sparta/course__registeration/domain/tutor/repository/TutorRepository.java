package com.sparta.course__registeration.domain.tutor.repository;

import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
}
