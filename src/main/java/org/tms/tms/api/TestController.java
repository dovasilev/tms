package org.tms.tms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tms.tms.dao.Test;
import org.tms.tms.dto.TestDto;
import org.tms.tms.services.TestService;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@Tag(name = "Test", description = "the Test in TMS API")
public class TestController {

    private TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Operation(summary = "Get all Test in all Suite", description = "Return all Test in all Projects", tags = { "Test" })
    @GetMapping(value = "/testsInProject/{projectId}")
    public Collection<Test> getTestInProject(@PathVariable Long projectId) {
        return testService.getAllByProjectId(projectId);
    }


    @Operation(summary = "Get all Test in Suite", description = "Return all Test in all Projects", tags = { "Test" })
    @GetMapping(value = "/testsInSuite/{suiteId}")
    public Collection<Test> getTestInSuite(@PathVariable Long suiteId) {
        return testService.getAllBySuiteId(suiteId);
    }

    @Operation(summary = "Get all Test in Suite and Child Suites", description = "Return all Test in all Projects", tags = { "Test" })
    @GetMapping(value = "/testsInSuiteAndChildSuites/{suiteId}")
    public Collection<Test> getTestsInParentSuiteAndChildSuites(@PathVariable Long suiteId) {
        return testService.getAllAndChildBySuiteId(suiteId);
    }

    @Operation(summary = "Add Test", description = "Return created Test", tags = { "Test" })
    @PostMapping(value = "/test", consumes = "application/json")
    public Test addTest(@Validated @RequestBody TestDto testDto) {
        Test result = testService.insert(testDto);
        return result;
    }

    @Operation(summary = "Update Test", description = "Return updated Test", tags = { "Suite" })
    @PutMapping(value = "/test/{testId}", consumes = "application/json")
    public Test updateTest(@PathVariable Long testId, @Validated @RequestBody TestDto testDto) {
        Test result = testService.updateTest(testId,testDto);
        return result;
    }

    @Operation(summary = "Delete Test", description = "", tags = { "Test" })
    @DeleteMapping(value = "/test/{testId}")
    public void deleteTest(@PathVariable Long testId) {
        testService.del(testId);
    }

    @Operation(summary = "Delete Test", description = "", tags = { "Test" })
    @DeleteMapping(value = "/testByProject/{projectId}")
    public void deleteTestsByProject(@PathVariable Long projectId) {
        testService.delAllByProject(projectId);
    }

}
