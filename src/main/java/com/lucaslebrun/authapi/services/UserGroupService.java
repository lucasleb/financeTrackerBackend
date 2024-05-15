package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserGroupService {
    private final UserGroupRepository userGroupRepository;

    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public List<UserGroup> findGroupsByUser(User user) {
        List<UserGroup> groupList = userGroupRepository.findByMembersContaining(user);
        groupList.forEach(group -> Hibernate.initialize(group.getMembers()));
        return groupList;
    }

    public Optional<UserGroup> findGroupById(Integer groupId) {
        Optional<UserGroup> groupOptional = userGroupRepository.findById(groupId);
        groupOptional.ifPresent(group -> Hibernate.initialize(group.getMembers()));
        groupOptional.ifPresent(group -> Hibernate.initialize(group.getAdmin()));
        return groupOptional;
    }

    public UserGroup save(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }

    public void delete(UserGroup userGroup) {
        userGroupRepository.delete(userGroup);
    }

    public void deleteById(Integer groupId) {
        userGroupRepository.deleteById(groupId);
    }

}
