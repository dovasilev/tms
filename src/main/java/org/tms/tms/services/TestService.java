package org.tms.tms.services;

import org.springframework.stereotype.Service;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.TestDto;
import org.tms.tms.repo.TestRepo;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {

    private TestRepo testRepo;

    public TestService(TestRepo testRepo) {
        this.testRepo = testRepo;
    }

    public Test getById(Long id){
        return testRepo.getOne(id);
    }

    public List<Test> getAll(){
        return testRepo.findAll();
    }

    public List<Test> getAllByProjectId(Long id){
        return testRepo.findAll().stream().filter(x->x.getProjectId().getId().equals(id)).collect(Collectors.toList());
    }

    // TODO: 18.07.2020 Надо сделать чтобы искались все вложенные сьюты и вытаскивались все тесты даже из вложенных
    public List<Test> getAllBySuiteId(Long id){
        List<Test> tests = new ArrayList<>();
        return testRepo.findAll().stream().filter(x->x.getProjectId().getId().equals(id)).collect(Collectors.toList());
    }

    @Transactional
    public synchronized Test insert(TestDto testDto){
        Test test = new Test();
        test.setTitle(testDto.getTitle());
        test.setDescription(testDto.getDescription());
        test.setStatus(testDto.getStatus());
        test.setSteps(testDto.getSteps().getStepsList().toString());
        test.setAutomated(testDto.getAutomated());
        testRepo.save(test);
        return test;
    }

    @Transactional
    public synchronized void del(Long id){
        Test test = testRepo.getOne(id);
        testRepo.delete(test);
    }

    @Transactional
    public Test updateData(Long dataId, TestDto testDto) {
        Test test = testRepo.getOne(dataId);
        test.setTitle(testDto.getTitle());
        test.setDescription(testDto.getDescription());
        test.setStatus(testDto.getStatus());
        test.setSteps(testDto.getSteps().toString());
        test.setAutomated(testDto.getAutomated());
        Test updatedData = testRepo.save(test);
        return updatedData;
    }




}
