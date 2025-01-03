# 일정 관리 프로젝트 

ERD 구조 
![일정관리](https://github.com/user-attachments/assets/83034fb8-c775-4d5d-94b8-22e758907692)

테이블의 설명


회원(Member) 테이블


id: 기본 키

user_id: 고유 사용자 ID

user_name: 사용자 이름

password: 사용자 비밀번호

user_email: 사용자 이메일

role: 역할 (예: ADMIN, USER)

created_time: 생성 시간

updated_time: 수정 시간

다른 테이블과의 관계:
알림(Notification) 테이블에서 user_id를 참조
통계(Statistics) 테이블에서 user_id를 참조


스케줄(Schedule) 테이블

id: 기본 키

contents: 일정 내용

start_time: 시작 시간

end_time: 종료 시간

progress_status: 진행 상태 (예: COMPLETE, PROGRESS, INCOMPLETE)

user_id: **회원(Member)** 의 id를 참조하는 외래 키

category_id: **카테고리(Category)** 의 id를 참조하는 외래 키

parent_schedule_id: 자기 자신을 참조하는 외래 키 (하위 일정)

created_time: 생성 시간

updated_time: 수정 시간

다른 테이블과의 관계:
**회원(Member)** 과 관계: user_id
**카테고리(Category)** 와 관계: category_id
**태그(Tag)** 와 관계: 중간 테이블(Schedule_Tag)을 통해 연결
**첨부파일(Attachment)** 과 관계: schedule_id


카테고리(Category) 테이블

id: 기본 키

name: 카테고리 이름

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: category_id (스케줄 테이블에서 참조)

태그(Tag) 테이블

id: 기본 키

name: 태그 이름

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: 중간 테이블(Schedule_Tag)을 통해 다대다 관계 설정


첨부파일(Attachment) 테이블

id: 기본 키

schedule_id: **스케줄(Schedule)** 의 id를 참조하는 외래 키

file_name: 파일 이름

file_url: 파일 URL

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: schedule_id

알림(Notification) 테이블

id: 기본 키

user_id: **회원(Member)** 의 id를 참조하는 외래 키

message: 알림 메시지

is_read: 읽음 여부

created_time: 생성 시간

다른 테이블과의 관계:

**회원(Member)** 과 관계: user_id

통계(Statistics) 테이블

id: 기본 키

user_id: **회원(Member)** 의 id를 참조하는 외래 키

schedule_count: 일정 개수

tag_count: 태그 개수

last_updated: 마지막 업데이트 시간

다른 테이블과의 관계:

**회원(Member)** 과 관계: user_id
