package com.example

import com.example.internal.dummyRealization.PasswordErrorDescription
import com.example.models.Credentials

interface PasswordStorage {
  fun storeCredentials(credentials: Credentials)

  fun checkCredentials(credentials: Credentials): PasswordErrorDescription
}