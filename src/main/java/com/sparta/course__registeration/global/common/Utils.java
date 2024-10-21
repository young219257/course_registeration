package com.sparta.course__registeration.global.common;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;

import java.util.List;

public class Utils {

    // 수업에 예약 가능한 시간대인지 확인
    public static boolean isAvailableForClass(TimeSlot timeSlot, List<TimeSlot> allSlots, ClassPath classPath) {
        if (!timeSlot.isAvailable()) {
            return false; // 사용 불가한 시간대는 제외
        }

        if (classPath.equals(ClassPath.SIXTY)) {
            // 연속된 시간대가 있는지 확인 (for-loop 사용)
            return hasNextAvailableSlot(timeSlot, allSlots);
        }

        // 30분 수업은 현재 시간대만 사용 가능
        return classPath.equals(ClassPath.THIRTY);
    }

    // 연속된 타임 슬롯이 있는지 확인
    private static boolean hasNextAvailableSlot(TimeSlot currentSlot, List<TimeSlot> allSlots) {
        for (TimeSlot nextSlot : allSlots) {
            if (nextSlot.getTutor().equals(currentSlot.getTutor()) &&
                    nextSlot.getStartTime().isEqual(currentSlot.getEndTime()) &&
                    nextSlot.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}
