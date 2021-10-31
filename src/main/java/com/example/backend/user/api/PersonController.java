package com.example.backend.user.api;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.user.service.PersonService;

@RequestMapping("/user")
@RestController
public class PersonController {
	private final PersonService personService;
	@Autowired
	public PersonController(PersonService personService) {
		this.personService = personService;
	}
	
	@PostMapping("/addShoppingInfo")
	public @ResponseBody Object addShoppingInfo(@RequestBody Map<String, String> reqBody) {
		int userId = Integer.valueOf(reqBody.get("userId"));
		String type = reqBody.get("type");
		String company = reqBody.get("company");
		int id = Integer.valueOf(reqBody.get("id"));
		String address = reqBody.get("address");
		try {
			personService.addShoppingInformation(userId, type, company, id, address);
			return (new HashMap<String, String>()).put("message", "Values successfully added");
			
		} catch (SQLException e) {
			return (new HashMap<String, String>()).put("message", "Bad input");
		} catch (Exception e) {
			return (new HashMap<String, String>()).put("message", "Internal server error");
		}
	}

}
