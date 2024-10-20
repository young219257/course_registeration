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
import java.time.LocalDateTime;
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
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tutor_id",nullable = false)
    private Tutor tutor;

    @OneToMany(mappedBy = "timeslot",cascade = CascadeType.ALL)
    private List<Lesson> lessons;


    public static TimeSlot of(Tutor tutor, LocalDateTime availableTimeSlot) {
        return TimeSlot.builder().
                tutor(tutor).
                startTime(availableTimeSlot).
                endTime(availableTimeSlot.plusMinutes(30)).
                isAvailable(true).
                build();
    }

    public void updateIsAvailable() {
        this.isAvailable = !this.isAvailable;
    }

}
