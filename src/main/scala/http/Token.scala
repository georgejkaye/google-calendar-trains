package http

def getClientIdFromFile(): String =
  io.Source.fromFile("client").getLines().next()

def getOAuthTokenFromFile(): String =
  io.Source.fromFile("token").getLines().next()

def getClientSecretFromFile(): String =
  io.Source.fromFile("secret").getLines().next()
