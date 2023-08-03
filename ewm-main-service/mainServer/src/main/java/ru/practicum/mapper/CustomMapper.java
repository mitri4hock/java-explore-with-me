package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserSortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.User;

@Mapper
public interface CustomMapper {
    CustomMapper INSTANCE = Mappers.getMapper(CustomMapper.class);

    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserSortDto toUserSortDto(User user);

    Compilation toCompilation(NewCompilationDto newCompilationDto);
}
