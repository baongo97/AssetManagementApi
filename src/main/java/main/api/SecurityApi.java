package main.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import main.dao.SecurityDao;
import main.dto.SecurityValue;

@RestController
@RequestMapping("/security")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class SecurityApi {
	@Autowired
	SecurityDao securityDao;
	
	@GetMapping(value = "/current") 
	public List<SecurityValue> getCurrentHoldingSecurity(@RequestParam("accountId") int accountId) {
		return securityDao.getAllSecurityByAccountId(accountId);
	}
}
