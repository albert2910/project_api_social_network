package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * use:
 *  EmployeeMapper.MAPPER.toEntity(dto);
 *  EmployeeMapper.MAPPER.toList(list);
 */
@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper( UserMapper.class );

    UserInfo toEntity(RegisterRequest dto);
    RegisterRequest toDto(UserInfo entity);
//    Page<EmployeeDTO> toList(Page<Employee> list);
}
