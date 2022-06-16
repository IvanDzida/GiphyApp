package com.yolo.db

interface DbMapper<DTO, Domain> {

    abstract fun fromDto(dto: DTO): Domain

    open fun toDto(domain: Domain): DTO? {
        return null
    }

    open fun fromDtoList(list: List<DTO>?): List<Domain>? {
        return list?.map { fromDto(it) }
    }
}