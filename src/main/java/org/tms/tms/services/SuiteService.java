package org.tms.tms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tms.tms.dao.Suite;
import org.tms.tms.dto.ProjectDto;
import org.tms.tms.dto.SuiteChild;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.mappers.ProjectMapper;
import org.tms.tms.repo.SuiteRepo;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuiteService {

    private SuiteRepo suiteRepo;
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;

    private TestService testService;

    @Autowired
    private void setTestService(TestService testService) {
        this.testService = testService;
    }

    public SuiteService(SuiteRepo suiteRepo, ProjectService projectService) {
        this.suiteRepo = suiteRepo;
        this.projectService = projectService;
    }

    public List<Suite> getAllSuitesByProject(Long projectId) {
        return suiteRepo.findAllSuiteByProject(projectId);
    }

    public List<Suite> getAllChildSuitesBySuite(Long suiteId) {
        return suiteRepo.findAllChildSuitesBySuite(suiteId);
    }

    public List<Suite> getChildSuitesBySuite(Long suiteId) {
        return suiteRepo.findChildSuitesBySuite(suiteId);
    }

    public Suite getSuiteById(Long suiteId) {
        return suiteRepo.findById(suiteId).get();
    }

    @Transactional
    public synchronized Suite insertSuite(SuiteDto suiteDto) {
        ProjectDto project = projectService.getProjectById(suiteDto.getProjectId());
        Suite parentSuite = null;
        if (suiteDto.getParentId() != null) {
            parentSuite = getSuiteById(suiteDto.getParentId());
        }
        Suite suite = new Suite();
        suite.setTitle(suiteDto.getTitle());
        suite.setDescription(suiteDto.getDescription());
        suite.setProjectId(projectMapper.projectDtoToProject(project));
        suite.setParentId(parentSuite);
        return suiteRepo.save(suite);
    }

    @Transactional
    public synchronized Suite updateSuite(Long suiteId, SuiteDto suiteDto) {
        Suite suite = getSuiteById(suiteId);
        ProjectDto project = projectService.getProjectById(suiteDto.getProjectId());
        Suite parentSuite = null;
        if (suiteDto.getParentId() != null) {
            parentSuite = getSuiteById(suiteDto.getParentId());
        }
        suite.setTitle(suiteDto.getTitle());
        suite.setDescription(suiteDto.getDescription());
        suite.setProjectId(projectMapper.projectDtoToProject(project));
        suite.setParentId(parentSuite);
        return suiteRepo.save(suite);
    }

    @Transactional
    public synchronized void deleteSuite(Long suiteId) {
        Suite suite = getSuiteById(suiteId);
        suiteRepo.delete(suite);
    }


    public List<SuiteChild> suiteChild(Long projectId) {
        List<SuiteChild> suiteChildren = new LinkedList<>();
        Collection<Suite> allSuite = getAllSuitesByProject(projectId);
        List<Suite> parent = allSuite.stream().filter(x -> x.getParentId() == null).collect(Collectors.toList());
        List<Suite> childs = allSuite.stream().filter(x -> x.getParentId() != null).collect(Collectors.toList());
        parent.forEach(x -> {
            suiteChildren.add(new SuiteChild(
                    x,
                    children(x, childs).stream().collect(Collectors.toList()),
                    allChildren(childs, x).stream().collect(Collectors.toList()),
                    testService.getAllBySuiteId(x.getId())));
        });
        return suiteChildren;
    }

    private Collection<SuiteChild> children(Suite parent, List<Suite> childs) {
        Collection<SuiteChild> suiteChildren = new LinkedList<>();
        childs.stream()
                .filter(y -> y.getParentId() == parent)
                .collect(Collectors.toList())
                .forEach(x -> {
                    suiteChildren.add(
                            new SuiteChild(
                                    x,
                                    children(x, childs).stream().collect(Collectors.toList()),
                                    allChildren(childs, x).stream().collect(Collectors.toList()),
                                    testService.getAllBySuiteId(x.getId())));
                });
        return suiteChildren;
    }

    private Collection<Suite> allChildren(Collection<Suite> suites, Suite parentSuite) {
        Collection<Suite> result = new LinkedList<>();
        Collection<Suite> child = suites.stream().filter(x -> x.getParentId() == parentSuite).collect(Collectors.toList());
        child.forEach(x -> {
            result.add(x);
            allChildren(suites, x).forEach(y -> {
                result.add(y);
            });
        });
        return result;
    }
}
