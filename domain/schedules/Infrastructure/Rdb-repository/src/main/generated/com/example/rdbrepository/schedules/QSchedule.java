package com.example.rdbrepository.schedules;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = 648450094L;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final StringPath contents = createString("contents");

    public final NumberPath<Integer> endTime = createNumber("endTime", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> parentScheduleId = createNumber("parentScheduleId", Long.class);

    public final EnumPath<com.example.enumerate.schedules.ProgressStatus> progressStatus = createEnum("progressStatus", com.example.enumerate.schedules.ProgressStatus.class);

    public final NumberPath<Integer> scheduleDay = createNumber("scheduleDay", Integer.class);

    public final NumberPath<Integer> scheduleMonth = createNumber("scheduleMonth", Integer.class);

    public final NumberPath<Integer> startTime = createNumber("startTime", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSchedule(String variable) {
        super(Schedule.class, forVariable(variable));
    }

    public QSchedule(Path<? extends Schedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedule(PathMetadata metadata) {
        super(Schedule.class, metadata);
    }

}

