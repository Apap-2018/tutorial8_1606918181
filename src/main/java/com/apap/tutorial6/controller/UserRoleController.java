package com.apap.tutorial6.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial6.model.PasswordModel;
import com.apap.tutorial6.model.UserRoleModel;
import com.apap.tutorial6.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST) 
	public String addUserSubmit(@ModelAttribute UserRoleModel user, Model model) {
		String message = "";
		if(this.validatePassword(user.getPassword())) {
			userService.addUser(user);
			message = null;
				
		}
		else {
			message = "Password minimal memiliki 8 karakter, mengandung angka dan huruf";
		}
		model.addAttribute("message", message);
		return "home";
		
	}
	
	//Latihan no 2: Tambahkan fitur update password yang dapat diakses oleh semua role user
	@RequestMapping(value="/updatePassword", method = RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel password, Model model, RedirectAttributes redirect) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		
		if (this.validatePassword(password.getPasswordLama()) && this.validatePassword(password.getPasswordBaru()) && this.validatePassword(password.getPasswordKonfirmasi())) {
			if (password.getPasswordKonfirmasi().equals(password.getPasswordBaru())) {
				if (passwordEncoder.matches(password.getPasswordLama(), user.getPassword())) {
					userService.updatePassword(user, password.getPasswordBaru());
					message = "Password berhasil diubah";
				}
				else {
					message = "Error! Password lama anda salah";
				}
			}
			else {
				message =	 "Error! Konfirmasi ulang password anda";
			}
		}
		else {
			message = "Password minimal memiliki 8 karakter, mengandung angka dan huruf";
		}
		
		ModelAndView modelAndView = new ModelAndView("redirect:/");
		redirect.addFlashAttribute("message", message);
		return modelAndView;
	}

	public boolean validatePassword(String password) {
		if (password.length()>=8 && Pattern.compile("[a-zA-Z]").matcher(password).find() && Pattern.compile("[0-9]").matcher(password).find()) {
			return true;
		}
		return false;
	}

}
