package com.ecomm.rewards.dao;

import com.ecomm.rewards.entity.TblCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerDao extends JpaRepository<TblCustomer, Integer> {
}
