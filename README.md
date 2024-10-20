# 🛒수강신청 api

## 1. 프로젝트 실행 방법

## 2. 프로젝트 설계 배경

### 📌 API

#### ● Tutor API
| Method | Endpoint                           | Description                     | Request                                                                     | Response Example                                      |
|--------|------------------------------------|---------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------|
| POST   | /api/tutors/{tutorId}/timeslots   | 수업 가능 시간대 생성         | {<br>  "availableTimeSlots": [<br>    "2023-06-07T12:00:00Z",<br>    "2023-06-07T12:30:00Z"<br>  ]<br>} | {<br>  "statusCode": 200,<br>  "message": "시간대 생성 성공"<br>} |
| DELETE | /api/tutors/{tutorId}/timeslots   | 수업 가능 시간대 삭제         | {<br>  "timeSlot": "2023-06-12T12:00:00Z"<br>}                          | {<br>  "statusCode": 200,<br>  "message": "시간대 삭제 성공"<br>} |

#### ● Student API
| Method | Endpoint                           | Description                     | Request                                                                     | Response Example                                      |
|--------|------------------------------------|---------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------|
| GET    | /api/lessons/timeslots             | 수업 가능 시간대 조회         | {<br>  "startDate": "2023-06-12T00:00:00Z",<br>  "endDate": "2023-06-15T00:00:00Z",<br>  "classPath": "SIXTY"<br>} | {<br>  "statusCode": 200,<br>  "message": "수업 가능 시간대 조회 성공",<br>  "data": [<br>    {"availableTimeSlot": "2023-06-12T14:00:00Z"},<br>    {"availableTimeSlot": "2023-06-14T20:00:00Z"}<br>  ]<br>} |
| GET | /api/lessons/tutors  | 수업 가능 튜터 조회         | {
"timeSlot": "2023-06-12T14:00:00Z",
"classPath" : "THIRTY"
}                          | {
    "statusCode": 200,
    "message": "수업 가능한 튜터 조회 성공",
    "data": [
        {
            "tutorId": 2,
            "tutorName": "Jane Smith"
        }
    ]
} |


### 주요 기능
### 데이터베이스
● Erd : https://www.erdcloud.com/d/impWPBYKqvDYHnMJx
