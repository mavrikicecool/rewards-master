package com.ecomm.rewards.controller;

import com.ecomm.rewards.dto.RewardDto;
import com.ecomm.rewards.exception.RewardsException;
import com.ecomm.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {
    @Autowired
    RewardService rewardService;

    @GetMapping("/get-reward/{customerID}")
    public ResponseEntity<?> getRewardPoint(@PathVariable Integer customerID) {
        try {
            RewardDto rewardPoints = rewardService.findRewardPoints(customerID);
            return new ResponseEntity<>(rewardPoints, HttpStatus.OK);
        }catch (RewardsException re){
            return ResponseEntity.badRequest().body(re.getErrorMessage());
        }
    }

}
