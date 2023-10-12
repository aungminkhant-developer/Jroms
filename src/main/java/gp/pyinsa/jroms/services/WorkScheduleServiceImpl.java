package gp.pyinsa.jroms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.querydsl.core.util.BeanUtils;


import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.WorkScheduleDto;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.WorkSchedule;
import gp.pyinsa.jroms.repositories.WorkScheduleRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final WorkScheduleRepository workScheduleRepository;

    // Constructor injection
    @Autowired
    public WorkScheduleServiceImpl(WorkScheduleRepository workScheduleRepository) {
        this.workScheduleRepository = workScheduleRepository;
    }

    @Override
    public WorkSchedule createSchedule(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    @Override
    public List<WorkSchedule> findAllSchedules() {
        return workScheduleRepository.findAll();
    }

    @Override
    public void deleteWorkSchedule(Short id) {
        workScheduleRepository.deleteById(id);
    }

    

    

    @Override
    public void updateWorkSchedule(WorkSchedule updateWorkSchedule) {
        // TODO Auto-generated method stub
        WorkSchedule workSchedule = workScheduleRepository.findById(updateWorkSchedule.getId()).orElse(null);
        if(workSchedule !=null){
            System.out.println("Saving");
            CustomBeanUtils.copyNonNullProperties(updateWorkSchedule,workSchedule,"id");
            System.out.println(workSchedule);
            WorkSchedule savedSchedule = workScheduleRepository.saveAndFlush(workSchedule);
            System.out.println("SAved schedule: " + savedSchedule);
            System.out.println("Saved");
        }
    }

    @Override
    public List<WorkSchedule> getActiveWorkSchedules(Long id) {
       return workScheduleRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public WorkSchedule getById(Short id) {
        // TODO Auto-generated method stub
       return workScheduleRepository.getById(id);
    }

   
}
