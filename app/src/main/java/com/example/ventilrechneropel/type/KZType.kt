package com.example.ventilrechneropel.type

import kotlinx.serialization.Serializable

@Serializable
enum class KZType {
    KZ4, KZ8 ,
    KZ12, KZ16,
    KZ20, KZ24X, KZ27X,
    KZ30X, KZ32X, KZ35X, KZ38X,
    KZ41X, KZ43X, KZ47,
    KZ51, KZ55, KZ59
}