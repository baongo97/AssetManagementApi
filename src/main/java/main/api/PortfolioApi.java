package main.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import main.dao.PortfolioDao;
import main.dto.PortfolioValue;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PortfolioApi {
	
	@Autowired
	PortfolioDao portfolioDao;
	
	@GetMapping(value = "/current") 
	public PortfolioValue getPortfolioValue(@RequestParam("portfolioId") int portfolioId) {
		PortfolioValue portfoliovalue = portfolioDao.getCurrentPortfolioValueById(portfolioId);
		return portfoliovalue;
	}

	@GetMapping(value = "/history") 
	public List<PortfolioValue> getPortfolioOverTime(@RequestParam("portfolioId") int portfolioId,
			@RequestParam("interval") String interval) {
		return portfolioDao.getPortfolioValueOverTime(portfolioId, interval);
	}
}
