# 🛒수강신청 api


## 1. 프로젝트 설계
### 📌 사용 기술
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Mockito

  
### 📌 주요 기능

#### ● Tutor API
**✔️ 수업 가능 시간대 생성**
- 튜터가 여러 개의 시간대를 선택하여 시간대를 생성할 수 있습니다.<br>

**✔️ 수업 가능 시간대 삭제**
- 튜터가 특정 시간대를 삭제할 수 있습니다.
- 튜터는 한 번에 하나의 시간대만 삭제할 수 있습니다.

#### ● Student API
**✔️ 수업 가능 시간대 조회**
- 학생이 특정 기간과 수업 길이를 설정하여 등록된 수업 가능 시간대를 조회할 수 있습니다.<br>
- 이미 예약된 시간대(isAvailable)는 조회되지 않습니다.<br>

  • 수업 길이 : 30분<br>
    30분 단위로 예약되지 않은 시간대 조회<br>
    
  • 수업 길이 : 60분<br>
    선택한 시간대로부터 30분 후의 수업이 가능한 튜터의 시간대 조회
   <br>
   <br>

   
**✔️ 수업 가능 튜터 조회**
- 학생이 특정 시간대와 수업 길이를 설정하여 수업 가능한 튜터를 조회할 수 있습니다.
- 이미 예약된 시간대(isAvailable)는 조회되지 않습니다.<br>
  
    • 수업 길이 : 30분<br>
    30분 단위로 수업이 가능한 튜터 조회<br>
    
    • 수업 길이 : 60분<br>
    선택한 시간대로부터 30분 후의 수업이 가능한 튜터 조회
   <br>
    
**✔️ 수강 신청**
- 학생이 원하는 튜터와 시간에 수강 신청을 할 수 있습니다.<br>
- 동일한 시간대에 중복 신청은 불가능합니다.<br>
- 이미 예약된 시간대는 신청이 불가능합니다.<br>
 
**✔️ 신청한 수업 조회**
- 학생이 자신이 신청한 수업의 목록을 조회할 수 있습니다.<br>

<br>

### 📌 데이터베이스
● Erd 주소 : https://www.erdcloud.com/d/impWPBYKqvDYHnMJx<br>
(링크 오류 시 첨부된 erd 사진 파일 참조)
<br>

### 📌 API

#### ● Tutor API

| Method | Endpoint                           | Description                     | Request                                                                     | Response Example                                      |
|--------|------------------------------------|---------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------|
| POST   | /api/tutors/{tutorId}/timeslots   | 수업 가능 시간대 생성         | {<br>  "availableTimeSlots": [<br>    "2023-06-07T12:00:00Z",<br>    "2023-06-07T12:30:00Z"<br>  ]<br>} | {<br>  "statusCode": 200,<br>  "message": "시간대 생성 성공"<br>} |
| DELETE | /api/tutors/{tutorId}/timeslots   | 수업 가능 시간대 삭제         | {<br>  "timeSlot": "2023-06-12T12:00:00Z"<br>}                          | {<br>  "statusCode": 200,<br>  "message": "시간대 삭제 성공"<br>} |

#### ● Student API

| Method | Endpoint                     | Description                  | Request                                                                     | Response Example                                      |
|--------|------------------------------|------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------|
| GET    | /api/lessons/timeslots       | 수업 가능 시간대 조회      | {<br>  "startDate": "2023-06-12T00:00:00Z",<br>  "endDate": "2023-06-15T00:00:00Z",<br>  "classPath": "SIXTY"<br>} | {<br>  "statusCode": 200,<br>  "message": "수업 가능 시간대 조회 성공",<br>  "data": [<br>    {"availableTimeSlot": "2023-06-12T14:00:00Z"},<br>    {"availableTimeSlot": "2023-06-14T20:00:00Z"}<br>  ]<br>} |
| GET    | /api/lessons/tutors          | 수업 가능 튜터 조회        | {<br>  "timeSlot": "2023-06-12T14:00:00Z",<br>  "classPath": "THIRTY"<br>}                          | {<br>  "statusCode": 200,<br>  "message": "수업 가능한 튜터 조회 성공",<br>  "data": [<br>    {<br>      "tutorId": 2,<br>      "tutorName": "Jane Smith"<br>    }<br>  ]<br>} |
| POST   | /api/lessons/apply            | 수강 신청                   | {<br>  "studentId": 2,<br>  "timeSlot": "2023-06-12T14:00:00Z",<br>  "tutorId": 2,<br>  "classPath": "THIRTY"<br>} | {<br>  "statusCode": 200,<br>  "message": "수강 신청 성공"<br>} |
| GET    | /api/lessons/my-lessons       | 신청한 수업 조회            | {<br>  "studentId": 2<br>}                                               | {<br>  "statusCode": 200,<br>  "message": "신청한 수업 조회 성공",<br>  "data": [<br>    {<br>      "lessonId": 1,<br>      "tutorName": "Jane Smith",<br>      "timeSlot": "2023-06-12T14:00:00Z",<br>      "classPath": "THIRTY"<br>    }<br>  ]<br>} |

### 📌 단위 테스트
✔️ 본 프로젝트는 자동화된 테스트를 사용하여 안전성을 보장합니다. 
   - 각 domain의 계층별 단위 테스트를 제공합니다.

<br>

## 2. 프로젝트 실행 방법
1) Java 설치 확인

   - `java -version` : `java version "21.0.4"`
   - 설치 방법: [Java 다운로드](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) 참고

2) Gradle 설치 확인

   - `gradle -v` : `3.3.4`
   - 설치방법: [Gradle 다운로드](https://gradle.org/releases/) 참고
   
3) 의존성(dependnecies) 설치

   - `./gradlew build`
  
4) 데이터베이스 설정

   - 데이터베이스 연결 정보를 `application.properties` 파일에 다음과 같이 설정합니다.

 ```properties
# 데이터베이스 연결 설정
spring.datasource.url=jdbc:mysql://localhost:3306/course_registration
spring.datasource.username=username
spring.datasource.password=password

# 데이터베이스 초기화 설정
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:init.sql
# JPA ??
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul
```
5) 애플리케이션 실행
   - `./gradlew bootRun`
   - 포트번호(8081) 확인하여, 브라우저 실행
<br>


