package com.example.internal.dummyRealization

import com.example.CredentialsCheckResult
import com.example.PasswordStorage
import com.example.models.Credentials


class DummyPasswordStorage : PasswordStorage {
  override fun storeCredentials(credentials: Credentials) {

  }

  override fun checkCredentials(credentials: Credentials): CredentialsCheckResult {
    return CredentialsCheckResult.SUCCESS
  }
}