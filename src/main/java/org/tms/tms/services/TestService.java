package org.tms.tms.services;

import org.springframework.stereotype.Service;
import org.tms.tms.dao.Project;
import org.tms.tms.dao.Suite;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.TestDto;
import org.tms.tms.repo.TestRepo;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TestService {

    private TestRepo testRepo;
    private SuiteService suiteService;
    private ProjectService projectService;

    public TestService(TestRepo testRepo, SuiteService suiteService, ProjectService projectService) {
        this.testRepo = testRepo;
        this.suiteService = suiteService;
        this.projectService = projectService;
    }

    public Test getById(Long id) throws NoSuchElementException {
        return testRepo.findById(id).get();
    }

    public List<Test> getAll() {
        return testRepo.findAll();
    }

    public List<Test> getAllByProjectId(Long id) {
        return testRepo.findAllTestsByProject(id);
    }

    public List<Test> getAllBySuiteId(Long id) {
        return testRepo.findAllTestsBySuite(id);
    }

    public List<Test> getAllAndChildBySuiteId(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        suiteService.getAllChildSuiteBySuite(id).stream().forEach(x -> ids.add(x.getId()));
        return testRepo.findAllTestsBySuites(ids);
    }

    @Transactional
    public synchronized Test insert(TestDto testDto) {
        Test test = new Test();
        Project project = projectService.getProjectById(testDto.getProjectId());
        Suite suite = null;
        if (testDto.getSuiteId() != null) {
            suite = suiteService.getSuiteById(testDto.getSuiteId());
        }
        test.setTitle(testDto.getTitle());
        test.setProjectId(project);
        test.setSuiteId(suite);
        test.setDescription(testDto.getDescription());
        test.setStatus(testDto.getStatus());
        test.setSteps(testDto.getSteps());
        test.setAutomated(testDto.getAutomated());
        testRepo.save(test);
        return test;
    }

    @Transactional
    public synchronized void del(Long id) {
        Test test = getById(id);
        testRepo.delete(test);
    }

    @Transactional
    public synchronized Test updateTest(Long testId, TestDto testDto) {
        Test test = getById(testId);
        Project project = projectService.getProjectById(testDto.getProjectId());
        Suite suite = null;
        if (testDto.getSuiteId() != null) {
            suite = suiteService.getSuiteById(testDto.getSuiteId());
        }
        test.setTitle(testDto.getTitle());
        test.setProjectId(project);
        test.setSuiteId(suite);
        test.setDescription(testDto.getDescription());
        test.setStatus(testDto.getStatus());
        test.setSteps(testDto.getSteps());
        test.setAutomated(testDto.getAutomated());
        testRepo.save(test);
        return test;
    }


}
