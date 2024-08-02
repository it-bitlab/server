package bitlab.app

trait Codes {
  object EnvCodes{
    val NotSet = " is not set"
  }
  object ErrorCodes  {
    val UnreachableCode = "Unreachable code detected."
    val InvalidAuthenticationType = "Invalid authentication type"
    val BearerTokenNotProvided  = "Bearer Token not provided"
    val TokenExpiredOrInvalid = "Token expired or invalid"
    val UserDoesntHaveRole = "User doesn't have required role"
    val UnidentifiedMessageFromActor = "Unidentified message received from actor"
  }
  object TextCodes{
    val Undefined = "undefined"
    val Removed = "removed"
    val Updated = "updated"
    val Forwarded = "forwarded"
    val ForwardedWithRoles = "forwarded with rolesRequired"
    val CodeError = "CODE_ERROR"
  }
}
