package com.github.tvbox.kotlin.data.entities

/**
 * git版本
 */
data class GitRelease(
    val version: String = "0.0.0",
    val downloadUrl: String = "",
    val description: String = "",
)
