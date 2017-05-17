package v1.book

import javax.inject.{Inject, Provider}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying book information.
  */
case class BookResource(id: String, link: String, title: String, body: String)

object BookResource {

  /**
    * Mapping to write a BookResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[BookResource] {
    def writes(book: BookResource): JsValue = {
      Json.obj(
        "id" -> book.id,
        "link" -> book.link,
        "title" -> book.title,
        "body" -> book.body
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[BookResource]]
  */
class BookResourceHandler @Inject()(
                                     routerProvider: Provider[BookRouter],
                                     bookRepository: BookRepository)(implicit ec: ExecutionContext) {

  def create(bookInput: BookFormInput): Future[BookResource] = {
    val data = BookData(BookId("999"), bookInput.title, bookInput.body)
    // We don't actually create the book, so return what we have
    bookRepository.create(data).map { id =>
      createBookResource(data)
    }
  }

  def lookup(id: String): Future[Option[BookResource]] = {
    val bookFuture = bookRepository.get(BookId(id))
    bookFuture.map { maybeBookData =>
      maybeBookData.map { bookData =>
        createBookResource(bookData)
      }
    }
  }

  def find: Future[Iterable[BookResource]] = {
    bookRepository.list().map { bookDataList =>
      bookDataList.map(bookData => createBookResource(bookData))
    }
  }

  private def createBookResource(p: BookData): BookResource = {
    BookResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }

}
