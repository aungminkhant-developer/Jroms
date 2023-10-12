// package gp.pyinsa.jroms.configs;

// import java.util.Properties;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.JavaMailSenderImpl;

// @Configuration
// public class MailConfig {

//     @Bean
//     public JavaMailSender javaMailSender() {
//         JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();

//         Properties properties = new Properties();
//         properties.put("mail.smtp.starttls.enable", "true");
//         properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
//         mailSenderImpl.setJavaMailProperties(properties);

//         return mailSenderImpl;
//     }
// }
