package v1.book

import javax.inject.Inject

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class BookFormInput(title: String, body: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class BookController @Inject()(
                                action: BookAction,
                                handler: BookResourceHandler)(implicit ec: ExecutionContext)
    extends Controller {

  private val form: Form[BookFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(BookFormInput.apply)(BookFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { book =>
        Ok(Json.toJson(book))
      }
    }
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonBook()
    }
  }

  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(id).map { book =>
        Ok(Json.toJson(book))
      }
    }
  }

  private def processJsonBook[A]()(
      implicit request: BookRequest[A]): Future[Result] = {
    def failure(badForm: Form[BookFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: BookFormInput) = {
      handler.create(input).map { book =>
        Created(Json.toJson(book)).withHeaders(LOCATION -> book.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
