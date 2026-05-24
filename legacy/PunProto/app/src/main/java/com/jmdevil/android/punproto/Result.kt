// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
data class Result(val OK: Boolean, val detail: String = "")
val ResultOK = Result(true, "")
