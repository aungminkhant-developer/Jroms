package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.dtos.WorkScheduleDto;
import gp.pyinsa.jroms.models.WorkSchedule;

public interface WorkScheduleService {

    WorkSchedule createSchedule(WorkSchedule workSchedule);

    List<WorkSchedule> findAllSchedules();

    List<WorkSchedule> getActiveWorkSchedules(Long id);

    void updateWorkSchedule(WorkSchedule workSchedule);

    void deleteWorkSchedule(Short id);

    WorkSchedule getById(Short id);
}
