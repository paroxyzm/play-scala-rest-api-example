package v1.book

import javax.inject.Inject

import play.api.http.HttpVerbs
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * A wrapped request for book resources.
  *
  * This is commonly used to hold request-specific information like
  * security credentials, and useful shortcut methods.
  */
class BookRequest[A](request: Request[A], val messages: Messages)
    extends WrappedRequest(request)

/**
  * The default action for the Book resource.
  *
  * This is the place to put logging, metrics, to augment
  * the request with contextual data, and manipulate the
  * result.
  */
class BookAction @Inject()(messagesApi: MessagesApi)(
    implicit ec: ExecutionContext)
    extends ActionBuilder[BookRequest]
    with HttpVerbs {

  type BookRequestBlock[A] = BookRequest[A] => Future[Result]

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: BookRequestBlock[A]): Future[Result] = {
    if (logger.isTraceEnabled()) {
      logger.trace(s"invokeBlock: request = $request")
    }

    val messages = messagesApi.preferred(request)
    val future = block(new BookRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}
