package com.example.models

fun<T> createSuccessResponse(data: T): Response {
  return SuccessResponse(true, data)
}

fun createFailureResponse(error: ErrorDescription): Response {
  return FailureResponse(false, error)
}