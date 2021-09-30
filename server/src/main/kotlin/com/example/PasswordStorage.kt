package com.example

import com.example.models.Credentials

enum class PasswordErrorDescription {
  NO_SUCH_USER,
  INCORRECT_PASSWORD,
  SUCCESS
}

interface PasswordStorage {
  fun storeCredentials(credentials: Credentials)

  fun checkCredentials(credentials: Credentials): PasswordErrorDescription
}