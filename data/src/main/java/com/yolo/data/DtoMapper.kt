package com.yolo.data

interface DtoMapper<Domain, DTO> {
    fun toDomain(dto: DTO): Domain
    //fun fromDomain(domain: Domain) : DTO
}