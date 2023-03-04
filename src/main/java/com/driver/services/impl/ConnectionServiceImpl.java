package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user = userRepository2.findById(userId).get();
        countryName.toUpperCase();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }else if(countryName.equals(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }else{
            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            if(user.getServiceProviderList() == null){
                throw new Exception("Unable to connect");
            }
            boolean availability = false;
            for(ServiceProvider serviceProvider: serviceProviderList){
                List<Country> countryList = serviceProvider.getCountryList();
                for(Country country: countryList){
                    if(countryName.equals(country.getCountryName().toString())){
                        availability = true;
                        break;
                    }
                }
                if(availability){
                    break;
                }
            }
            if(!availability){
                throw new Exception("Unable to connect");
            }
            int serviceProviderId =  Integer.MAX_VALUE;
            ServiceProvider serviceProvider = null;
            Country cntry = null;
            for(ServiceProvider sp: serviceProviderList){
                List<Country> countryList = sp.getCountryList();
                for(Country country: countryList){
                    if(countryName.equals(country.getCountryName().toString())){
                        if(sp.getId()<serviceProviderId){
                            serviceProviderId = sp.getId();
                            serviceProvider = sp;
                            cntry = country;
                        }
                    }
                }
            }
            Connection connection = new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider);
            String countryCode = cntry.getCode();
            String maskedIp = countryCode + "." + serviceProvider.getId() + "." + userId;
            user.setMaskedIp(maskedIp);;
            user.setConnected(true);
            user.getConnectionList().add(connection);
            serviceProvider.getConnectionList().add(connection);
            serviceProviderRepository2.save(serviceProvider);
            userRepository2.save(user);
            return user;
        }

    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }else{
            user.setConnected(false);
            user.setMaskedIp(null);
            userRepository2.save(user);
            return user;
        }
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();
        if(receiver.getMaskedIp() != null){
            String countryCode = receiver.getMaskedIp().substring(0,3);
            if(countryCode.equals(sender.getOriginalCountry().getCode())){
                return sender;
            }else{
                String countryName = "";

                if (countryCode.equals(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (countryCode.equals(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (countryCode.equals(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (countryCode.equals(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (countryCode.equals(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();
                try{
                    User updatedSender = connect(senderId,countryName);
                    return updatedSender;
                }catch (Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
        }else{
            if(receiver.getOriginalCountry().equals(sender.getOriginalCountry())){
                return sender;
            }else{
                String countryName = receiver.getOriginalCountry().getCountryName().toString();
                try{
                    User updatedSender = connect(senderId,countryName);
                    return updatedSender;
                }catch (Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
        }
    }
}
