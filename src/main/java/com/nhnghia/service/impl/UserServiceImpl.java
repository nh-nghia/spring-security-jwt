package com.nhnghia.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhnghia.dao.UserRoleDao;
import com.nhnghia.entity.SystemUser;
import com.nhnghia.repository.UserRepository;
import com.nhnghia.service.EmailService;
import com.nhnghia.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private EmailService emailService;

	/**
	 * just run without @Transactional
	 */
	@Override
	public Map<String, Object> save(SystemUser systemUser) {

		Map<String, Object> mapResult = new HashMap<>();

		String username = systemUser.getUsername();

		SystemUser theUser = userRepository.findByUsernameIgnoreCase(username);
		if (theUser == null) {
			SystemUser newUser = new SystemUser();
			newUser.setUsername(username);
			newUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
			newUser.setFirstname(systemUser.getFirstname());
			newUser.setLastname(systemUser.getLastname());
			newUser.setAvatar(systemUser.getAvatar());
			userRepository.save(newUser);
			userRoleDao.createUserRole(newUser.getId(), 1L);
			emailService.sendVerifyEmail(username);
			mapResult.put("msg", "Please verify you email");
			mapResult.put("status", true);
		} else {
			mapResult.put("msg", "Looks like you already have an account with that email address");
			mapResult.put("status", false);
		}
		return mapResult;
	}

}
