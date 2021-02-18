package fr.abes.lnevent.security.services;

import fr.abes.lnevent.dto.User;


public interface IUserService {

    User createUser(User user);

    User findUserByUserName(User user);

}
