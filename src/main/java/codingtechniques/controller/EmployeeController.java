package codingtechniques.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import codingtechniques.model.Employee;
import codingtechniques.service.EmailService;
import codingtechniques.service.EmployeeService;
import codingtechniques.service.OTPService;
import jakarta.servlet.http.HttpSession;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;
    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeService.findEmployees());
        return "employees"; // Thymeleaf template: employees.html
    }

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("employee", new Employee());
        return "registration"; // Thymeleaf template: registration.html
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/updateEmployee/{id}")
    public String updateEmployee(Model model, @PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id); // Fetch employee by ID
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get()); // Add employee to the model
            return "updateForm"; // Return the update form template
        } else {
            return "redirect:/employees"; // Redirect to the employees list if employee not found
        }
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
    // Logout (invalidate session and redirect to login page)
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/admin/login"; // Redirect to the login page
    }

    @PostMapping("/user/login")
    public String handleAdminLogin(
            @RequestParam String username,
            @RequestParam String email,
            Model model,
            HttpSession session) {

        // Check if the username exists
        List<Employee> employee = employeeService.findByFirstname(username);

        // Check if the email matches for that user
        if (!employee.isEmpty() && employee.get(0).getEmail().equals(email)) {
        	 String otp = otpService.generateOTP(); // Generate OTP
             otpService.storeOTP(email, otp); // Store OTP
             emailService.sendOTPEmail(email, otp); // Send OTP via email

             session.setAttribute("email", email);
             session.setAttribute("otp", otp); // Store OTP in session
             // Store email in session
             return "otp-verification"; // Redirect to OTP verification page
        } else {
            model.addAttribute("error", "Invalid username or email");
            return "userlogin";
        }
    }

    // Step 2: User enters OTP
    @PostMapping("/user/verify-otp")
    public String handleOTPVerification(
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("email");

        if (email != null && otpService.validateOTP(email, otp)) {
            session.removeAttribute("email"); // Clear email from session
            session.setAttribute("user", employeeService.findByemail(email).get(0)); // Store user in session
            return "redirect:/user/home"; // Redirect to user home page
        } else {
            model.addAttribute("error", "Invalid OTP");
            return "otp-verification"; // Return to OTP verification page with error message
        }
    }
//    //Auto fill OTP
//    @PostMapping("/user/verify-otp")
//    public String handleOTPVerification(
//            @RequestParam String otp,
//            HttpSession session,
//            Model model) {
//
//        String email = (String) session.getAttribute("email");
//
//        if (email != null && otpService.validateOTP(email, otp)) {
//            session.removeAttribute("email"); // Clear email from session
//            session.removeAttribute("otp"); // Clear OTP from session
//            session.setAttribute("user", employeeService.findByemail(email).get(0)); // Store user in session
//            return "redirect:/user/home"; // Redirect to user home page
//        } else {
//            model.addAttribute("error", "Invalid OTP");
//            return "otp-verification"; // Return to OTP verification page with error message
//        }
//    }
//    @GetMapping("/user/get-otp")
//    @ResponseBody
//    public String getOTP(HttpSession session) {
//        return (String) session.getAttribute("otp"); // Return OTP from session
//    }
    // User home page
    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "userhome"; // Display user home page
        } else {
            return "redirect:/user/login"; // Redirect to login page if user is not logged in
        }
    }
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        // Fetch the logged-in user from the session
        Employee user = (Employee) session.getAttribute("user");

        if (user != null) {
            model.addAttribute("user", user); // Add user details to the model
            return "profile"; // Return the profile template
        } else {
            return "redirect:/user/login"; // Redirect to login page if user is not logged in
        }
    }
}
