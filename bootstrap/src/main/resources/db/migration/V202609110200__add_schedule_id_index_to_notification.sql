-- deleteReminderByScheduleId(scheduleId)가 schedule_id, notification_type으로 필터링하는데
-- 인덱스가 없어서 매번 notification 테이블 풀스캔이 발생했다 (2026-09-11 CAS 재테스트 원인 분석 중 발견)
CREATE INDEX idx_notification_schedule_id_type ON notification(schedule_id, notification_type);
