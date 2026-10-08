package com.urlshortener.shortener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.urlshortener.shortener.dto.CreateLinkResponse;
import com.urlshortener.shortener.model.ShortLink;


@Mapper(componentModel = Mapper.ComponentModel.SPRING)
public interface ShortLinkMapper {
    CreateLinkResponse toResponse(ShortLink link);
}