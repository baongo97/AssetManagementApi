package main.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import main.dao.AssetDao;
import main.dto.AssetPercentage;
import main.dto.AssetValue;

@RestController
@RequestMapping("/asset")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AssetApi {
	
	@Autowired
	AssetDao assetDao;
	
	@GetMapping(value = "/current") 
	public AssetValue getPortfolioValue(@RequestParam("accountId") int accountId) {
		return assetDao.getCurrentAssetValueByAccountId(accountId);
	}
	
	@GetMapping(value = "/history/first") 
	public AssetValue getFirstPortfolioValue(@RequestParam("accountId") int accountId) {
		return assetDao.getFirstAssetValueByAccountId(accountId);
	}
	
	@GetMapping(value = "/percentage")
	public List<AssetPercentage> getAssetPercentage(@RequestParam("accountId") int accountId){
		return assetDao.getAssetPercentageByAccountId(accountId);	
	}
}
