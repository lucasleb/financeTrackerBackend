package com.lucaslebrun.authapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucaslebrun.authapi.entities.TransactionCategory;
import java.util.List;
import com.lucaslebrun.authapi.entities.UserGroup;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Integer>{
    
    List<TransactionCategory> findByUserGroup(UserGroup userGroup);

    List<TransactionCategory> findByUserGroupAndName(UserGroup userGroup, String name);
   
}
