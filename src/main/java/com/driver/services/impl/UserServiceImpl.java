package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
          User user = new User();
          user.setUsername(username);
          user.setPassword(password);
          countryName.toUpperCase();

        if(!(countryName == "IND" || countryName == "USA" || countryName == "CHI" || countryName == "JPN" || countryName == "AUS")){
            throw new Exception("Country not found");
        }
        Country country = new Country();
        if(countryName == "IND"){
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }
        if(countryName == "USA"){
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        if(countryName == "JPN"){
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        if(countryName == "AUS"){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }
        if(countryName == "CHI"){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }
        country.setUser(user);
        user.setCountry(country);
        String ip = country.getCode() + "." + user.getId();
        user.setOriginalIp(ip);
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        List<User> userList = serviceProvider.getUserList();
        userList.add(user);
        serviceProvider.setUserList(userList);
        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        user.setServiceProviderList(serviceProviderList);
        userRepository3.save(user);
        return user;
    }

}
