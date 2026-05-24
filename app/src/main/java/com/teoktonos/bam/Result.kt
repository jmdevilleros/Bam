package com.teoktonos.bam

/**
 * Simple result object used throughout the rule validation system.
 * Mirrors the original design from PunProto.
 */
data class Result(
    val OK: Boolean,
    val detail: String = ""
)

val ResultOK = Result(OK = true)
