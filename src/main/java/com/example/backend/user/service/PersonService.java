package com.example.backend.user.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.backend.user.dao.PersonDao;

@Service
public class PersonService {
	private final PersonDao personDao;
	@Autowired
	public PersonService(@Qualifier("realPersonDao") PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public int addShoppingInformation(int userId, String type, String company, int id, String address) throws SQLException {
		return personDao.addShoppingInformation(userId, type, company, id, address);
		
	}
}
