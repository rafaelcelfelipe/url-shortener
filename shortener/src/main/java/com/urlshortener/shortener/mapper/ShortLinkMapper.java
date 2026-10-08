package com.urlshortener.shortener.mapper;

import com.urlshortener.shortener.dto.ShortLinkResponse;
import com.urlshortener.shortener.model.ShortLink;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShortLinkMapper {

    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + link.getCode())")
    ShortLinkResponse toResponse(ShortLink link, @Context String baseUrl);
}