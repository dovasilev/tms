package org.tms.tms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tms.tms.dao.Test;
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

    @Operation(summary = "Get all Test in all Suite", description = "Return all Data in all Projects", tags = { "Data" })
    @GetMapping(value = "/data")
    public Collection<Test> getTestInProject() {
        return testService.getAll();
    }
}
