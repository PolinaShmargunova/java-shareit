package ru.practicum.user.service;

import ru.practicum.user.dto.ShortUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static ShortUserDto toShortUserDto(User user) {
        return ShortUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static Collection<UserDto> listToUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}

