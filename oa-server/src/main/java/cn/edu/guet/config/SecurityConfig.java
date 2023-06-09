package cn.edu.guet.config;

import cn.edu.guet.security.JwtAuthenticationFilter;
import cn.edu.guet.security.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

/**
 * Security的自定义各种配置
 * 1、设置身份验证组件
 * 2、配置HTTP的访问规则
 * 3、注册AuthenticationManager实现类到Spring的IoC容器，以便过滤器可以注入该实例
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /*
    重写2个方法
     */
    @Autowired
    @Qualifier("UserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    /**
     * 自定义身份验证组件
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new JwtAuthenticationProvider(userDetailsService));
    }

    /**
     * 配置访问规则
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用CSRF攻击（因为用的是token，默认基于cookie）
        http.cors().and().csrf().disable()
                .authorizeRequests()
                // 跨域预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 放行登录
                .antMatchers("/login").permitAll()
                //放行忘记密码功能
                .antMatchers("/api/user/user/**").permitAll()
                //放行静态资源
                .antMatchers("/static/**").permitAll()
                //放行图片上传
                .antMatchers("/addContractPhoto").permitAll()
                //放行采购单导入
                .antMatchers("/purchaseContract/purchaseImportExcel").permitAll()
                //放行海运单导入
                .antMatchers("/shippingContract/shippingImportExcel").permitAll()
                //放行办公经费单导入
                .antMatchers("/officeExpense/officeExpenseImportExcel").permitAll()
                //放行采购付款单导入
                .antMatchers("/purchasePaymentContract/purchasePaymentImportExcel").permitAll()
                //放行加工付款单导入
                .antMatchers("/processPaymentContract/processPaymentImportExcel").permitAll()
                //放行物流付款单导入
                .antMatchers("/logisticsPaymentContract/logisticsPaymentImportExcel").permitAll()
                //放行采购付款单导入
                .antMatchers("/api/logistics/logisContractImportExcel").permitAll()
                //放行采购单导出
                .antMatchers("/purchaseContract/purchaseExportExcel").permitAll()
                //放行销售单导出
                .antMatchers("/api/saleContract/exportExcel").permitAll()
                //放行销售单导出
                .antMatchers("/api/saleContract/exportExcel").permitAll()
                //放行物流单导出
                .antMatchers("/api/logistics/exportExcel").permitAll()
                //放行查看细则单导出
                .antMatchers("/finance/incomeSpendExportExcel").permitAll()
//                .antMatchers("/**").permitAll()
                // 其他所有请求需要身份认证
                .anyRequest().authenticated();
        http.headers().frameOptions().disable();
        // 退出登录处理器
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
