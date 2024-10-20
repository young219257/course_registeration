package com.sparta.course__registeration.domain.lesson.repository;

import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByStudentId(Long studentId);
}
