package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Integer payableAmount =0;
        if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE)){
            payableAmount = 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            payableAmount = 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else {
            payableAmount = 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
        }

        int userId = subscriptionEntryDto.getUserId();
        User user = userRepository.findById(userId).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(payableAmount);

        user.setSubscription(subscription);
        userRepository.save(user);

        return payableAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        Integer diffPayableAmount =0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)){
            Integer proCharges = 800 + 250 * subscription.getNoOfScreensSubscribed();
            Integer eliteCharges = 1000 + 350 * subscription.getNoOfScreensSubscribed();
            diffPayableAmount = eliteCharges - proCharges;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(eliteCharges);
        }
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)){
            Integer proCharges = 800 + 250 * subscription.getNoOfScreensSubscribed();
            Integer basicCharges = 500 + 200 * subscription.getNoOfScreensSubscribed();
            diffPayableAmount = proCharges - basicCharges;
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(proCharges);
        }
        userRepository.save(user);

        return diffPayableAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        Integer revenue =0;
        for (Subscription subscription: subscriptionList){
            revenue += subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
