package com.urlshortener.statistics.mapper;

import com.urlshortener.statistics.dto.AccessCountResponse;
import com.urlshortener.statistics.model.LinkAccess;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LinkAccessMapper {

    AccessCountResponse toResponse(LinkAccess access);
}