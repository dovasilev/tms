package org.tms.tms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tms.tms.dao.Suite;
import org.tms.tms.dto.SuiteChild;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.services.SuiteService;
import org.tms.tms.web.model.ParentWebModel;
import org.tms.tms.web.model.SuiteWebModel;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@Tag(name = "Suite", description = "the Suite in TMS API")
public class SuiteController {

    private SuiteService suiteService;

    public SuiteController(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    @Operation(summary = "Get Suite", description = "Return Suite", tags = {"Suite"})
    @GetMapping(value = "/suite/{suiteId}")
    public ResponseEntity<Suite> getSuiteById(@PathVariable Long suiteId) {
        try {
            Suite suite = suiteService.getSuiteById(suiteId);
            return new ResponseEntity<>(suite, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all Suites in Project", description = "Return all Suites in Project", tags = {"Suite"})
    @GetMapping(value = "/suites/{projectId}")
    public List<Suite> getAllSuitesByProject(@PathVariable Long projectId) {
        return suiteService.getAllSuitesByProject(projectId);
    }

    @Operation(summary = "Get all Child Suites in Suite", description = "Return all Child Suites in Suite", tags = {"Suite"})
    @GetMapping(value = "/childAllSuites/{suiteId}")
    public Collection<Suite> getAllChildSuiteBySuite(@PathVariable Long suiteId) {
        return suiteService.getAllChildSuitesBySuite(suiteId);
    }

    @Operation(summary = "Get Child Suites in Suite", description = "Return Child Suites in Suite", tags = {"Suite"})
    @GetMapping(value = "/childSuites/{suiteId}")
    public Collection<Suite> getChildSuiteBySuite(@PathVariable Long suiteId) {
        return suiteService.getChildSuitesBySuite(suiteId);
    }

    @Operation(summary = "Add Suite", description = "Return created Suite", tags = {"Suite"})
    @PostMapping(value = "/suite", consumes = "application/json")
    public Suite addSuite(@Validated @RequestBody SuiteDto suite) {
        Suite result = suiteService.insertSuite(suite);
        return result;
    }

    @Operation(summary = "Update Suite", description = "Return updated Suite", tags = {"Suite"})
    @PutMapping(value = "/suite/{suiteId}", consumes = "application/json")
    public Suite updateSuite(@PathVariable Long suiteId, @Validated @RequestBody SuiteDto suite) {
        Suite result = suiteService.updateSuite(suiteId, suite);
        return result;
    }

    @Operation(summary = "Delete Suite", description = "", tags = {"Suite"})
    @DeleteMapping(value = "/suite/{suiteId}")
    public void deleteSuite(@PathVariable Long suiteId) {
        suiteService.deleteSuite(suiteId);
    }

    @Operation(summary = "Get Hierarchy Suites in Project", description = "Return Hierarchy Suites in Project", tags = {"Suite"})
    @GetMapping(value = "/suitesHierarchy/{projectId}")
    public List<SuiteChild> getSuiteHierarchy(@PathVariable Long projectId) {
        return suiteService.suiteChild(projectId);
    }

    @Operation(summary = "Get Hierarchy Suites in Project", description = "Return Hierarchy Suites in Project", tags = {"Suite"})
    @GetMapping(value = "/suitesHierarchyNew/{projectId}")
    public List<ParentWebModel> getSuiteHierarchyNew(@PathVariable Long projectId) {
        return suiteService.suiteChildNew(projectId);
    }

}
