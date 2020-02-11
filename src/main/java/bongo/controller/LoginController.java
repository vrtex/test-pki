package bongo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import bongo.model.LoginModel;
import bongo.model.RegisterModel;
import bongo.service.DataBaseService;

import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Controller
public class LoginController
{

    @Autowired
    DataBaseService dataBaseService;

    @GetMapping("/aaa")
    public String loginForm(Model model, @ModelAttribute LoginModel loginModel, @ModelAttribute RegisterModel registerModel)
    {
        model.addAttribute("loginModel", new LoginModel());
        model.addAttribute("registerModel", new RegisterModel());
        return "main"; //view
    }

    @PostMapping("/aaa")
    public String loginModelSubmit(@Validated @ModelAttribute LoginModel loginModel, BindingResult result, final RedirectAttributes redirectAttributes, Model model, HttpSession session) throws URISyntaxException, SQLException
    {
        if (dataBaseService.checkUserLogin(loginModel.getEmail(), loginModel.getPassword())) {
            session.setAttribute("email", loginModel.getEmail());
            return "redirect:/query";
        }
        result.addError(new ObjectError("email", "Wrong Email and password combination."));
        model.addAttribute("registerModel", new RegisterModel());
        return "main";
    }

    @PostMapping("/register")
    public String registerModelSubmit(@Validated @ModelAttribute RegisterModel registerModel, BindingResult result, final RedirectAttributes redirectAttributes, Model model) throws URISyntaxException, SQLException
    {
        if (!registerModel.getPassword().equals(registerModel.getRePassword()))
            result.addError(new ObjectError("password", "Passwords didn't match."));
        try {
            if (dataBaseService.checkUserExist(registerModel.getEmail()))
                result.addError(new ObjectError("email", "User with email: " + registerModel.getEmail() + " already exist"));
        } catch (Exception e) {
            result.addError(new ObjectError("email", "Database connection problem"));
            System.out.println("DB exception: " + e.getMessage());
        }
        if (result.hasErrors()) {
            model.addAttribute("loginModel", new LoginModel());
            return "main";
        }
        dataBaseService.addUser(registerModel);
        redirectAttributes.addFlashAttribute("registerSuccess", "User with email: " + registerModel.getEmail() + " created successfully!");
        redirectAttributes.addFlashAttribute("loginModel", new LoginModel());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.removeAttribute("email");
        return "redirect:/";
    }
}
