package com.tecsup.app.micro.user.service;

import com.tecsup.app.micro.user.entity.UserEntity;
import com.tecsup.app.micro.user.mapper.UserMapper;
import com.tecsup.app.micro.user.dto.UserDto;
import com.tecsup.app.micro.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        List<UserEntity> entities = userRepository.findAll();
        return this.mapper.toDomain(entities);
    }

    public UserDto getUserById(Long id) {

        log.info("Fetching user with id: {} ", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return mapper.toDomain(userEntity);

    }

    @Transactional
    public UserDto createUser(UserDto user) {

        log.info("Creating new user : {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        UserEntity userEntity = mapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(userEntity);
        UserDto savedUser = mapper.toDomain(savedEntity);

        log.info("Saved user via Spring Data: {}", savedUser);

        return savedUser;
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDetails) {
        log.info("Updating user with id: {}", id);
        UserDto user = getUserById(id);
        UserEntity entityUser = mapper.toEntity(user);
        UserEntity entityUserUpdate = userRepository.save(entityUser);
        return mapper.toDomain(entityUserUpdate);

    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        UserDto user = getUserById(id);
        UserEntity entityUser = mapper.toEntity(user);
        UserEntity ue = new UserEntity();
        userRepository.delete(ue);
    }
}