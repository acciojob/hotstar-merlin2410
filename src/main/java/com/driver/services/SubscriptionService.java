package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        int totalAmountPaid = 0;
        if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.BASIC){
            totalAmountPaid = 500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        } else if (subscriptionEntryDto.getSubscriptionType()==SubscriptionType.PRO) {
            totalAmountPaid = 800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
        } else if (subscriptionEntryDto.getSubscriptionType()==SubscriptionType.ELITE) {
            totalAmountPaid = 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();
        }
        subscription.setTotalAmountPaid(totalAmountPaid);
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        return subscription.getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if(subscription.getSubscriptionType()==SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        int currentPaidAmount = subscription.getTotalAmountPaid();
        int newTotalAmount = 0;
        if(subscription.getSubscriptionType()==SubscriptionType.BASIC){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            newTotalAmount = 800 + 250*subscription.getNoOfScreensSubscribed();
            subscription.setTotalAmountPaid(newTotalAmount);
        } else if (subscription.getSubscriptionType()==SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            newTotalAmount = 1000 + 350*subscription.getNoOfScreensSubscribed();
            subscription.setTotalAmountPaid(newTotalAmount);
        }
        user.setSubscription(subscription);
        userRepository.save(user);
        return newTotalAmount-currentPaidAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalRevenue = 0;
        for(Subscription subscription: subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
