package bitlab.http

trait HttpActorResponse {
  case class SuccessTextResponse(value: String) extends HttpActorResponse
  case class ErrorTextResponse(value: String) extends HttpActorResponse
}
