package v1

import play.api.i18n.Messages

/**
  * Package object for book.  This is a good place to put implicit conversions.
  */
package object book {

  /**
    * Converts between BookRequest and Messages automatically.
    */
  implicit def requestToMessages[A](implicit r: BookRequest[A]): Messages = {
    r.messages
  }
}
