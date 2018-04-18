package test.example;

import org.springframework.stereotype.Component;

/**
 * Created by xiaodm on 2018/3/21.
 */
@Component
public class HiHystric  implements IServiceHi{
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry,invoke fail";
    }
}
