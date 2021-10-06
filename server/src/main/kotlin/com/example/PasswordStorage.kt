package com.example

import com.example.models.Credentials

enum class CredentialsCheckResult {
  INVALID_CREDENTIALS,
  SUCCESS
}

interface PasswordStorage {
  fun storeCredentials(credentials: Credentials)

  fun checkCredentials(credentials: Credentials): CredentialsCheckResult
}