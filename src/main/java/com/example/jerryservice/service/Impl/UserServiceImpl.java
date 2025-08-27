package com.example.jerryservice.service.Impl;



import com.example.jerryservice.repository.UserRepository;
import com.example.jerryservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public Long findUserIdByUsername(String username) {
        return userRepository.findIdByUsername(username);
    }

}

