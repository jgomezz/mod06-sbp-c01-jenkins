package com.tecsup.app.micro.user.mapper;

import com.tecsup.app.micro.user.entity.UserEntity;
import com.tecsup.app.micro.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDomain(UserEntity entity);

    UserEntity toEntity(UserDto domain);

    List<UserDto> toDomain(List<UserEntity> entities);

}
