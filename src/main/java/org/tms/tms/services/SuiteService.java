package org.tms.tms.services;

import org.springframework.stereotype.Service;
import org.tms.tms.dao.Project;
import org.tms.tms.dao.Suite;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.repo.SuiteRepo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SuiteService {

    private SuiteRepo suiteRepo;
    private ProjectService projectService;

    public SuiteService(SuiteRepo suiteRepo, ProjectService projectService) {
        this.suiteRepo = suiteRepo;
        this.projectService = projectService;
    }

    public List<Suite> getAllSuiteByProject(Long projectId){
        return suiteRepo.findAllSuiteByProject(projectId);
    }

    public List<Suite> getAllChildSuiteBySuite(Long suiteId){
        return suiteRepo.findAllChildSuitesBySuite(suiteId);
    }

    public Suite getSuiteById(Long suiteId)  {
        return suiteRepo.findById(suiteId).get();
    }

    @Transactional
    public synchronized Suite insertSuite(SuiteDto suiteDto)  {
        Project project = projectService.getProjectById(suiteDto.getProjectId());
        Suite parentSuite = null;
        if (suiteDto.getParentId()!=null){
            parentSuite = getSuiteById(suiteDto.getParentId());
        }
        Suite suite = new Suite();
        suite.setTitle(suiteDto.getTitle());
        suite.setDescription(suiteDto.getDescription());
        suite.setProjectId(project);
        suite.setParentId(parentSuite);
        return suiteRepo.save(suite);
    }

    @Transactional
    public synchronized Suite updateSuite(Long suiteId,SuiteDto suiteDto) {
        Suite suite = getSuiteById(suiteId);
        Project project = projectService.getProjectById(suiteDto.getProjectId());
        Suite parentSuite = null;
        if (suiteDto.getParentId()!=null){
            parentSuite = getSuiteById(suiteDto.getParentId());
        }
        suite.setTitle(suiteDto.getTitle());
        suite.setDescription(suiteDto.getDescription());
        suite.setProjectId(project);
        suite.setParentId(parentSuite);
        return suiteRepo.save(suite);
    }

    @Transactional
    public synchronized void deleteSuite(Long suiteId) {
        Suite suite = getSuiteById(suiteId);
        suiteRepo.delete(suite);
    }

    @Transactional
    public synchronized void deleteAllSuiteByProject(Long projectId) {
        suiteRepo.deleteAllByProject(projectId);
    }

}
