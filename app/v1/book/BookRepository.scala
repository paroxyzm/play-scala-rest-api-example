package v1.book

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

final case class BookData(id: BookId, title: String, body: String)

class BookId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object BookId {
  def apply(raw: String): BookId = {
    require(raw != null)
    new BookId(Integer.parseInt(raw))
  }
}

/**
  * A pure non-blocking interface for the BookRepository.
  */
trait BookRepository {
  def create(data: BookData): Future[BookId]

  def list(): Future[Iterable[BookData]]

  def get(id: BookId): Future[Option[BookData]]
}

/**
  * A trivial implementation for the Book Repository.
  */
@Singleton
class BookRepositoryImpl @Inject() extends BookRepository {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val bookList = List(
    BookData(BookId("1"), "title 1", "blog book 1"),
    BookData(BookId("2"), "title 2", "blog book 2"),
    BookData(BookId("3"), "title 3", "blog book 3"),
    BookData(BookId("4"), "title 4", "blog book 4"),
    BookData(BookId("5"), "title 5", "blog book 5")
  )

  override def list(): Future[Iterable[BookData]] = {
    Future.successful {
      logger.trace(s"list: ")
      bookList
    }
  }

  override def get(id: BookId): Future[Option[BookData]] = {
    Future.successful {
      logger.trace(s"get: id = $id")
      bookList.find(book => book.id == id)
    }
  }

  def create(data: BookData): Future[BookId] = {
    Future.successful {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
