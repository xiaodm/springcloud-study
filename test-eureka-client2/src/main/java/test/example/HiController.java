package test.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by xiaodm on 2018/3/20.
 */
@RestController
public class HiController {
    Logger log = LoggerFactory.getLogger(HiController.class);

    @Autowired
    IServiceHi iServiceHi;
    @RequestMapping(value = "/hic",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        log.info("service2 call sayHi");
        String rlt = "service-feign1 get by feign: " + iServiceHi.sayHiFromClientOne(name);
        log.info(rlt);
        return rlt;
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String callTest(@RequestParam String name){
        log.info("service2 call sayHi");
        return "service-feign1 get by feign: " + name;
    }
}
