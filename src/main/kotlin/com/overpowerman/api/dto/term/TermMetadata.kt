package com.overpowerman.api.dto.term

typealias TermVersion = String;
typealias S3DocVersion = String;

data class TermMetadata(
    val title: String,
    val versions: MutableMap<TermVersion, S3DocVersion>,
)
