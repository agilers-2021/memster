package com.example.internal.dummyRealization

import com.example.PasswordStorage
import com.example.models.Credentials

enum class PasswordErrorDescription {
  NO_SUCH_USER,
  INCORRECT_PASSWORD,
  SUCCESS
}


class DummyPasswordStorage : PasswordStorage {
  override fun storeCredentials(credentials: Credentials) {

  }

  override fun checkCredentials(credentials: Credentials): PasswordErrorDescription {
    return PasswordErrorDescription.SUCCESS
  }
}