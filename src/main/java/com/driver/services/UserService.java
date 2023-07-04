package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        int userAge = user.getAge();
        if(userAge <= 18)
            userAge = 18;
        else
            userAge = Integer.MAX_VALUE;
        int count =0;

        for (WebSeries webSeries: webSeriesList){
            if (user.getSubscription().equals(SubscriptionType.ELITE)
                    && (webSeries.getSubscriptionType().equals(SubscriptionType.ELITE)
                    || webSeries.getSubscriptionType().equals(SubscriptionType.PRO)
                    || webSeries.getSubscriptionType().equals(SubscriptionType.BASIC))){
                if(webSeries.getAgeLimit() <= userAge){
                    count++;
                }
            }
            else if (user.getSubscription().equals(SubscriptionType.PRO)
                    && (webSeries.getSubscriptionType().equals(SubscriptionType.PRO)
                    || webSeries.getSubscriptionType().equals(SubscriptionType.BASIC))){
                if(webSeries.getAgeLimit() <= userAge){
                    count++;
                }
            }
            else if (user.getSubscription().equals(SubscriptionType.BASIC)
                    && webSeries.getSubscriptionType().equals(SubscriptionType.BASIC)){
                if(webSeries.getAgeLimit() <= userAge){
                    count++;
                }
            }
        }

        return count;
    }


}
