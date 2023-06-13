package uk.yetanother.compact.device.adaptor.external.services.fuction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultFunctionHandler implements IFunctionHandler {
    @Override
    public String execute(String function, String attributes) {
        log.info(String.format("Asked to run function %s with attributes %s.", function, attributes));
        return function;
    }

}
