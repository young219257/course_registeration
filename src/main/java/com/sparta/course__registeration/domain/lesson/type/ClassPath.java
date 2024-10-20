package com.sparta.course__registeration.domain.lesson.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClassPath {
    THIRTY(30, "30분"),
    SIXTY(60, "60분");
    private final int classPath; // 수업 길이 (분)
    private final String description;

}
