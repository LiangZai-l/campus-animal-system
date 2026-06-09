package com.campus.animal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园流浪动物图鉴与动态打卡系统 — Spring Boot 启动类。
 * <p>
 * {@code @SpringBootApplication} 是一个复合注解，等效于：
 * <ul>
 *   <li>{@code @SpringBootConfiguration} — 标记为配置类（可定义 @Bean）</li>
 *   <li>{@code @EnableAutoConfiguration} — 启用 Spring Boot 自动配置，
 *       根据 classpath 中的 jar 依赖自动配置 DataSource、Spring MVC、Jackson 等</li>
 *   <li>{@code @ComponentScan} — 扫描当前包及其子包中的
 *       {@code @Component}、{@code @Service}、{@code @Repository}、{@code @Controller} 等注解</li>
 * </ul>
 * <p>
 * 启动流程（简要）：
 * <ol>
 *   <li>{@code SpringApplication.run()} 创建 ApplicationContext（IoC 容器）</li>
 *   <li>扫描并注册所有 @Component/@Service/@Controller/@Configuration 类</li>
 *   <li>执行自动配置（DataSource、Tomcat、Jackson 等）</li>
 *   <li>执行 CommandLineRunner（如 DataInitializer 初始化管理员）</li>
 *   <li>启动内嵌 Tomcat，监听 8080 端口</li>
 * </ol>
 */
@SpringBootApplication
public class CampusAnimalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusAnimalApplication.class, args);
    }
}
