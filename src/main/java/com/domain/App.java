package com.domain;
    /*
    * hxh
    * today
    */
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import com.didispace.swagger.EnableSwagger2Doc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableSwagger2Doc
@EnableTransactionManagement
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class).web(true).run(args);
    }
}