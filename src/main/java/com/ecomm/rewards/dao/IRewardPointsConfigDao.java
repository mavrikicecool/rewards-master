package com.ecomm.rewards.dao;

import com.ecomm.rewards.entity.TblRewardPointsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRewardPointsConfigDao extends JpaRepository<TblRewardPointsConfig, Integer> {
}
