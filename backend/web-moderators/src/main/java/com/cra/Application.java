package com.cra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);


//        ModeratorRepository moderatorRepository = context.getBean(ModeratorRepository.class);
//        UserRepository userRepository = context.getBean(UserRepository.class);
//        RoleRepository roleRepository = context.getBean(RoleRepository.class);
//
//        Role adminRole = new Role(UserRole.ADMIN);
//        Role moderatorRole = new Role(UserRole.MODERATOR);
//        Role userRole = new Role(UserRole.USER);
//
//        roleRepository.save(userRole);
//        roleRepository.save(moderatorRole);
//        roleRepository.save(adminRole);
//
//
//        Moderator moderator = new Moderator(
//                "admin@admin.ad", "$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi",
//                "Admin", "Adminovich",
//                adminRole
//        );
//
//        moderatorRepository.save(moderator);
//
//        User user = new User(
//                "user@user.us", "$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC",
//                "User", "Userovich", userRole
//        );
//
//        userRepository.save(user);
//
//        User expired = new User(
//                "expired@expired.ex", "$2a$10$PZ.A0IuNG958aHnKDzILyeD9k44EOi1Ny0VlAn.ygrGcgmVcg8PRK",
//                "Expired", "Expiredovich", userRole
//        );
//
//        userRepository.save(expired);
//        //check with curl - i - H "Content-Type: application/json" - X POST - '{"username":"user@user.us","password":"password"}' http:
//        //localhost:8080/api/auth
//
//        BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);
//
//        String password = encoder.encode("moderator");
//        System.out.println(password);
    }

}
