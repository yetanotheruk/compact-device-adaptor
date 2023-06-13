package uk.yetanother.compact.device.adaptor.web.function;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.yetanother.compact.device.adaptor.business.function.FunctionController;
import uk.yetanother.compact.device.adaptor.external.dto.FunctionRunResultDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/cda/function")
@RequiredArgsConstructor
public class FunctionWebController {

    private final FunctionController functionController;

    @PostMapping(path = "/{function}", produces = "application/json")
    public UUID executeFunction(@PathVariable("function") String function, @RequestBody(required = false) String attributes) {
        return functionController.execute(function, attributes);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public FunctionRunResultDto getFunctionResults(@PathVariable("id") UUID id) {
        return functionController.result(id);
    }

}
