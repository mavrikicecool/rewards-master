package com.ecomm.rewards.controller;

import com.ecomm.rewards.dto.RewardDto;
import com.ecomm.rewards.exception.RewardsException;
import com.ecomm.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.ecomm.rewards.constants.RewardErrorConstants.INVALID_CUSTOMER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RewardControllerTests {
    @Mock
    RewardService rewardService;

    @InjectMocks
    RewardController rewardController;

    @Test
    public void givenCustomerID_whenFound_thenReturnRewardPoints() {
        Integer customerID = 1;
        when(rewardService.findRewardPoints(customerID)).thenReturn(RewardDto.builder().rewardPoints("270.2").build());
        ResponseEntity<?> response = rewardController.getRewardPoint(customerID);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        RewardDto rewardDto = (RewardDto) response.getBody();
        assertNotNull(rewardDto);
        assertThat(rewardDto.getRewardPoints()).isEqualTo("270.2");
    }

    @Test
    public void givenCustomerID_whenNotFound_thenReturnException() {
        Integer customerID = 10;
        when(rewardService.findRewardPoints(customerID)).thenThrow(new RewardsException(INVALID_CUSTOMER_ID));
        ResponseEntity<?> response = rewardController.getRewardPoint(customerID);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(INVALID_CUSTOMER_ID);
    }
}
