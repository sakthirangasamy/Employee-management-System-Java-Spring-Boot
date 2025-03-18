package codingtechniques.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    // Display the admin login page
    @GetMapping("/admin/login")
    public String showAdminLoginPage() {
        return "adminlogin"; // Return the name of the HTML file (without .html)
    }
    // Display the admin login page
    @GetMapping("/user/login")
    public String showUserLoginPage() {
        return "userlogin"; // Return the name of the HTML file (without .html)
    }
    // Handle the admin login form submission
    @PostMapping("/admin/login")
    public String handleAdminLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        // Hardcoded credentials for demonstration
        if ("admin".equals(username) && "admin".equals(password)) {
            return "redirect:/admin/home"; // Redirect to admin home page
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "adminlogin"; // Return to login page with error message
        }
    }

    // Display the admin home page
    @GetMapping("/admin/home")
    public String showAdminHomePage() {
        return "adminhome"; // Return the name of the HTML file (without .html)
    }
}