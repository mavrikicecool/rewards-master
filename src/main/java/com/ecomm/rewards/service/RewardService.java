package com.ecomm.rewards.service;

import com.ecomm.rewards.dao.ICustomerDao;
import com.ecomm.rewards.dao.IOrderDao;
import com.ecomm.rewards.dao.IRewardPointsConfigDao;
import com.ecomm.rewards.dto.RewardDto;
import com.ecomm.rewards.entity.TblCustomer;
import com.ecomm.rewards.entity.TblOrder;
import com.ecomm.rewards.entity.TblRewardPointsConfig;
import com.ecomm.rewards.exception.RewardsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.ecomm.rewards.constants.RewardConstants.*;
import static com.ecomm.rewards.constants.RewardErrorConstants.INVALID_CUSTOMER_ID;
import static com.ecomm.rewards.constants.RewardErrorConstants.NO_TRANSACTION_AVAILABLE;

@Service
@Slf4j
public class RewardService {
    @Autowired
    ICustomerDao iCustomerDao;
    @Autowired
    IOrderDao iOrderDao;
    @Autowired
    IRewardPointsConfigDao iRewardPointsConfigDao;
    @Value("${back.month}")
    Integer backMonth;
    private static final DateFormat df = new SimpleDateFormat(YYYY_MM);
    private static final DecimalFormat df1 = new DecimalFormat(DECIMAL_FRACTION);


    public RewardDto findRewardPoints(Integer customerId) throws RewardsException {
        log.info("start findRewardPoints - {}", customerId);
        String rewardPoints;
        Optional<TblCustomer> customerOptional = iCustomerDao.findById(customerId);
        if (customerOptional.isEmpty())
            throw new RewardsException(INVALID_CUSTOMER_ID);

        TblCustomer tblCustomer = new TblCustomer();
        tblCustomer.setId(customerId);
        Date fromDate = findBackDate();
        List<TblOrder> tblOrderList = iOrderDao.findAllByCustomerIdAndCreatedOnGreaterThanEqual(tblCustomer, fromDate);


        if (tblOrderList.isEmpty())
            throw new RewardsException(NO_TRANSACTION_AVAILABLE);


        Map<String, List<TblOrder>> dateAmountMap = tblOrderList.stream()
                .collect(Collectors.groupingBy(data -> df.format(data.getCreatedOn()), Collectors.toCollection(ArrayList::new)));

        rewardPoints = df1.format(calculateRewards(dateAmountMap));
        log.info("end findRewardPoints - reward points - {} for customer id - {}", rewardPoints, customerId);
        return RewardDto.builder().rewardPoints(rewardPoints).build();
    }

    private Date findBackDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusMonths(backMonth);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Double calculateRewards(Map<String, List<TblOrder>> dateAmountMap) throws RewardsException {
        double rewardPoints = 0.0;
        int pointsFor100;
        int pointsFor50;
        List<TblRewardPointsConfig> rewardPointsConfigs = iRewardPointsConfigDao.findAll();
        if (rewardPointsConfigs.isEmpty()) {
            log.error("calculateRewards - could not find rewardPointsConfig");
            throw new RewardsException("Technical failure!");
        }
        Optional<Integer> optionalPointsFor100 = rewardPointsConfigs.stream().filter(data -> data.getBaseAmount() == AMOUNT_100).map(TblRewardPointsConfig::getPoint).findAny();
        Optional<Integer> optionalPointsFor50 = rewardPointsConfigs.stream().filter(data -> data.getBaseAmount() == AMOUNT_50).map(TblRewardPointsConfig::getPoint).findAny();

        if (optionalPointsFor100.isEmpty()) {
            log.error("calculateRewards - could not find rewardPoints for $100");
            throw new RewardsException("Technical failure!");
        }
        pointsFor100 = optionalPointsFor100.get();

        if (optionalPointsFor50.isEmpty()) {
            log.error("calculateRewards - could not find rewardPoints for $50");
            throw new RewardsException("Technical failure!");
        }
        pointsFor50 = optionalPointsFor50.get();
        for (Map.Entry<String, List<TblOrder>> entry : dateAmountMap.entrySet()) {
            List<TblOrder> orders = entry.getValue();
            Optional<Double> optionalTotalAmount = orders.stream().map(TblOrder::getAmount).reduce(Double::sum);
            if (optionalTotalAmount.isPresent()) {
                double totalAmount = optionalTotalAmount.get();
                if (totalAmount > AMOUNT_100) {
                    double amount100 = totalAmount - AMOUNT_100;
                    rewardPoints += amount100 * pointsFor100 + POINT_50;
                } else if (totalAmount > AMOUNT_50) {
                    double amount50 = totalAmount - AMOUNT_50;
                    rewardPoints += amount50 * pointsFor50;
                }
            }
        }

        return rewardPoints;
    }

}
