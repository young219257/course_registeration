package com.sparta.course__registeration.domain.lesson.entity;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.student.entity.Student;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassPath classPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tutor_id",nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id", nullable = false)
    private TimeSlot timeslot;


    public static Lesson of(ClassPath classPath, Tutor tutor, Student student, TimeSlot timeslot) {
        return Lesson.builder().
                classPath(classPath).
                tutor(tutor).
                student(student).
                timeslot(timeslot).
                build();
    }



}