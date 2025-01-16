package com.github.tvbox.kotlin.data.entities

data class UpdateRelease(

    val buildHaveNewVersion: Boolean = false,
    val downloadURL: String = "",
    val buildUpdateDescription: String = "",
    val version: String = "",
)