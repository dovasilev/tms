package org.tms.tms.web.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import org.tms.tms.dto.SuiteDto;
import org.tms.tms.web.view.ProjectView;

public class ConvertSuiteDivToSuiteDto implements Converter<ProjectView.SuiteDiv, Long> {

    SuiteDto suiteDto;
    ProjectView.SuiteDiv suiteDiv;

    @Override
    public Result<Long> convertToModel(ProjectView.SuiteDiv suiteDiv, ValueContext valueContext) {
        if (valueContext==null){
            return Result.ok(null);
        }
        try {
            // ok is a static helper method that creates a Result
            this.suiteDiv = suiteDiv;
            suiteDto = new SuiteDto();
            suiteDto.setParentId(suiteDiv.getSuite().getId());
            return Result.ok(suiteDto.getParentId());
        } catch (Exception e) {
            // error is a static helper method that creates a Result
            return Result.error("Выберите значение");
        }
    }

    @Override
    public ProjectView.SuiteDiv convertToPresentation(Long aLong, ValueContext valueContext) {
        return this.suiteDiv;
    }

}
