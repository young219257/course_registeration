# 🛒수강신청 api

### 1. 프로젝트 실행 방법

### 2. 프로젝트 설계 배경

#### API
##### Tutor API
| Method | Endpoint | Description                     | Request                                                                     | Response Example                                      |
|--------|--------------------------------------|---------------------------------|--------------------------------------------------------------|------------------------------------------------------|
| POST   | /api/tutors/{tutorId}/timeslots     | 수업 가능 시간대 생성         | {<br>  "availableTimeSlots": [<br>    "2023-06-07T12:00:00Z",<br>    "2023-06-07T12:30:00Z"<br>  ]<br>} | {<br>  "statusCode": 200,<br>  "message": "시간대 생성 성공"<br>} |
| GET   | /api/tutors/{tutorId}/timeslots                          | 수업 가능 시간대 삭제                   | {<br>  {
  "timeSlot": 
    <br>"2023-06-12T12:00:00Z"
}
<br>}         | {<br>  {
    "statusCode": 200,<br>
    "message": "시간대 삭제 성공"
}<br>}      |


#### 주요 기능
#### 데이터베이스
● Erd : https://www.erdcloud.com/d/impWPBYKqvDYHnMJx
