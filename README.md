# 일정 관리 프로젝트 

ERD 구조 
![일정관리 (1)](https://github.com/user-attachments/assets/19cb4ace-786d-414c-a971-238dd38195fb)



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

created_by: 가입자(회원 아이디)

updated_by: 수정자(회원 아이디)

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

schedule_month: 특정일정의 월

schedule_day: 특정일정의 일

created_time: 생성 시간

updated_time: 수정 시간

created_by: 가입자(회원 아이디)

updated_by: 수정자(회원 아이디)

다른 테이블과의 관계:
**회원(Member)** 과 관계: user_id
**카테고리(Category)** 와 관계: category_id
**태그(Tag)** 와 관계: 중간 테이블(Schedule_Tag)을 통해 연결
**첨부파일(Attachment)** 과 관계: schedule_id


카테고리(Category) 테이블

id: 기본 키

name: 카테고리 이름

parent_id: 상위 카테고리 (기본값은 null)

depth: 카테고리의 계층 (기본값은 0)

created_time: 생성 시간

updated_time: 수정 시간

created_by: 작성자(회원 아이디)

updated_by: 수정자(회원 아이디)

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: category_id (스케줄 테이블에서 참조)

태그(Tag) 테이블

id: 기본 키

name: 태그 이름

created_time: 생성 시간

updated_time: 수정 시간

created_by: 태그 생성자

updated_by: 태그 수정자

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: 중간 테이블(Schedule_Tag)을 통해 다대다 관계 설정
중간 테이블에 태그의 순서를 보장을 하기 위해서 position 컬럼을 추가.


첨부파일(Attachment) 테이블

id: 기본 키

schedule_id: **스케줄(Schedule)** 의 id를 참조하는 외래 키

file_name: 파일 이름

file_size: 파일 크기(최대 20MB)

origin_file_name: 원본 파일명

stored_file_name: 저장된 파일명(클라우드에 저장될 파일명)

created_time: 생성 시간

updated_time: 수정 시간

created_by: 업로드한 사람(회원 아이디)

updated_by: 수정자(회원 아이디)

다른 테이블과의 관계:
**스케줄(Schedule)** 과 관계: schedule_id

알림(Notification) 테이블

id: 기본 키

user_id: **회원(Member)** 의 id를 참조하는 외래 키

message: 알림 메시지

is_read: 읽음 여부

notice_type: 알림 종류

created_time: 생성 시간

다른 테이블과의 관계:

**회원(Member)** 과 관계: user_id

통계(Statistics) 테이블

id: 기본 키

user_id: **회원(Member)** 의 id를 참조하는 외래 키

schedule_count: 일정 개수

tag_count: 태그 개수

last_updated: 마지막 업데이트 시간

다른 테이블과의 관계

**회원(Member)** 과 관계: user_id
