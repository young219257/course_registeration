package com.sparta.course__registeration.domain.timeslot.entity;

import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="timeslots")
public class TimeSlot extends TimeStamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;


    @Column(nullable = false)
    private LocalDate startTime;

    @Column(nullable = false)
    private LocalDate endTime;

    @Column(nullable = false)
    private boolean isAvailable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tutor_id",nullable = false)
    private Tutor tutor;

    @OneToMany(mappedBy = "timeslots",cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}
