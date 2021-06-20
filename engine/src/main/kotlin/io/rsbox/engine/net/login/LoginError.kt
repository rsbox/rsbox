package io.rsbox.engine.net.login

import io.rsbox.engine.net.ServerStatus

class LoginError(val error: ServerStatus) : Exception()