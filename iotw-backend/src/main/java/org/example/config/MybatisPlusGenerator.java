package org.example.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;
import java.util.Collections;

/**
 * mp代码生成器
 *
 * @author hwshou
 * @date 2025/5/28  17:56
 */
public class MybatisPlusGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/iotw",
                        "root",
                        "123456")
                .globalConfig(builder -> builder
                        .author("hwshou")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd HH:mm")
                )
                .packageConfig(builder -> builder
                        .parent("org.example")
                        .entity("entity.dto")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                        .pathInfo(Collections.singletonMap(OutputFile.xml,System.getProperty("user.dir")+"/src/main/resources/mapper"))
                )
                .strategyConfig(builder -> builder
                        .addInclude("db_category",
                                "db_customer",
                                "db_good",
                                "db_sold",
                                "db_supplier",
                                "db_supply",
                                "db_warehouse")
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .enableFileOverride()
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .formatFileName("%s")

                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper")
                        .build()

                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .build()

                        .controllerBuilder()
                        .enableRestStyle()
                        .formatFileName("%sController")
                        .build()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
