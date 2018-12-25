package io.github.flyingdew.generator;

import org.junit.Test;
import org.mybatis.generator.api.ShellRunner;

import java.util.ArrayList;
import java.util.List;

public class MybatisGeneratorTest {
    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        list.add("-configfile");
        list.add("D:\\GitHub\\mybatis-generator-plugins\\src\\test\\resources\\generatorConfig.xml");
//        list.add("-tables");
//        list.add("user_teacher");
        list.add("-overwrite");
        list.add("-verbose");
        String[] args = list.toArray(new String[0]);
        ShellRunner.main(args);
    }
}