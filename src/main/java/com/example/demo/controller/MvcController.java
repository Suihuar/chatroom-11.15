package com.example.demo.controller;

import com.example.demo.entity.Chatroom;
import com.example.demo.entity.User;
import com.example.demo.entity.UserLogin;
import com.example.demo.service.ChatroomService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
public class MvcController {
    @Autowired
    private UserService userService;

    @Autowired
    private ChatroomService chatroomService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public String addNewUser(@Valid @ModelAttribute User user) {
        userService.save(user);
        return "Saved";
    }

    @RequestMapping("/")
    public String home() {
        System.out.println("Going home..."); // appear in the console
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        UserLogin userLogin = new UserLogin();
        model.addAttribute("userLogin", userLogin);
        return "login_form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserLogin userLogin, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userLogin", userLogin);
            return "login_form";
        } else {
            if (userService.login(userLogin)) {
                System.out.println("Login success");
                model.addAttribute("userLogin", userLogin);
                User currentUser = userService.getUserByEmail(userLogin.getEmail());
                System.out.println("login: " + currentUser.getName());
                session.setAttribute("currentUser", currentUser);
                return "redirect:/lobby";
            } else {
                System.out.println("Login failed");
                model.addAttribute("userLogin", userLogin);
                model.addAttribute("loginError", "Invalid email or password");

                return "login_form";
            }
        }
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        User user = new User();
        String[] professionList = new String[3];
        professionList[0] = "Developer";
        professionList[1] = "Designer";
        professionList[2] = "Tester";
        model.addAttribute("user", user);
        model.addAttribute("professionList", professionList);
        return "register_form";
    }

//    @PostMapping("/register")
//    public String submitForm(@Valid @ModelAttribute User user, BindingResult
//            bindingResult, Model model) {
////        System.out.println(user); // the user information is displayed in the console
//        if (bindingResult.hasErrors()) {
//            String[] professionList = new String[3];
//            professionList[0] = "Developer";
//            professionList[1] = "Designer";
//            professionList[2] = "Tester";
//            model.addAttribute("user", user);
//            model.addAttribute("professionList", professionList);
//            return "register_form";
//        } else {
//            model.addAttribute("user", user);
//            userService.save(user);
//            return "login";
//        }
//    }

    @PostMapping("/register")
    public String submitForm(@Valid @ModelAttribute User user,
                             BindingResult bindingResult,
                             @RequestParam("profileImage") MultipartFile profileImage,
                             Model model) {
        if (bindingResult.hasErrors()) {
            String[] professionList = new String[3];
            professionList[0] = "Developer";
            professionList[1] = "Designer";
            professionList[2] = "Tester";
            model.addAttribute("user", user);
            model.addAttribute("professionList", professionList);
            return "register_form";
        } else {
            if (!profileImage.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
                    Path path = Paths.get(uploadDir + fileName);
                    Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    user.setProfileImagePath(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 处理文件上传错误
                    model.addAttribute("fileError", "文件上传失败，请重试。");
                    return "register_form";
                }
            }

            userService.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/account")
    public String showAccountForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("user", currentUser);
        String[] professionList = new String[3];
        professionList[0] = "Developer";
        professionList[1] = "Designer";
        professionList[2] = "Tester";
        model.addAttribute("professionList", professionList);
        return "account";
    }

    @PostMapping("update")
    public String updateUser(@Valid @ModelAttribute User user,
                             BindingResult bindingResult,
                             Model model, HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @RequestParam("profileImage") MultipartFile profileImage) {
        if (bindingResult.hasErrors()) {
            String[] professionList = new String[3];
            professionList[0] = "Developer";
            professionList[1] = "Designer";
            professionList[2] = "Tester";
            model.addAttribute("user", user);
            model.addAttribute("professionList", professionList);
            return "account";
        } else {
            User currentUser = (User) session.getAttribute("currentUser");
            System.out.println("current user " + currentUser.getName() + " updated");
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPassword(user.getPassword());
            currentUser.setGender(user.getGender());
            currentUser.setProfession(user.getProfession());
            currentUser.setNote(user.getNote());
            currentUser.setBirthday(user.getBirthday());
            currentUser.setMarried(user.isMarried());

            // 处理头像上传
            if (!profileImage.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // 删除旧的头像文件（如果存在）
                    if (currentUser.getProfileImagePath() != null) {
                        Path oldFilePath = Paths.get(uploadDir + currentUser.getProfileImagePath().substring("/uploads/".length()));
                        Files.deleteIfExists(oldFilePath);
                    }

                    currentUser.setProfileImagePath(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    model.addAttribute("fileError", "文件上传失败，请重试。");
                    return "account";
                }
            }

            userService.save(currentUser);
            session.setAttribute("currentUser", currentUser);
            model.addAttribute("user", currentUser);
            redirectAttributes.addFlashAttribute("updateSuccess", "Account Information Update Success");
            return "redirect:/account";
        }
    }

    @GetMapping("/lobby")
    public String showLobby(Model model, HttpSession session) {
        List<Chatroom> chatrooms = chatroomService.getAllChatrooms();
        model.addAttribute("chatrooms", chatrooms);
        return "lobby";
    }

    @GetMapping("/chatroom")
    public String showChatroom() {
        return "chatroom";
    }
}