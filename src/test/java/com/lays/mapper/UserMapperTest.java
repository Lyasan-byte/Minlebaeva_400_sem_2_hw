package com.lays.mapper;

import com.lays.dto.UserDTO;
import com.lays.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toDTO_mapsUserFields() {
        User user = new User();
        user.setId(5L);
        user.setUsername("alice");

        UserDTO result = userMapper.toDTO(user);

        assertEquals(5L, result.getId());
        assertEquals("alice", result.getUsername());
    }

    @Test
    void toDTO_returnsNullForNullUser() {
        assertNull(userMapper.toDTO(null));
    }

    @Test
    void toEntity_mapsDtoFields() {
        UserDTO dto = new UserDTO(9L, "bob");

        User result = userMapper.toEntity(dto);

        assertEquals(9L, result.getId());
        assertEquals("bob", result.getUsername());
    }

    @Test
    void toEntity_returnsNullForNullDto() {
        assertNull(userMapper.toEntity(null));
    }
}
