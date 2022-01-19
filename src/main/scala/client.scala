import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import io.StdIn._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object client extends App {
  //println("Creating a New Socket")
  val s = new Socket("localhost", 3536)
  //println("Socket Creation Success")
  val input = new BufferedReader(new InputStreamReader(s.getInputStream))
  val output = new PrintStream(s.getOutputStream)
  Future {
    while (true) {
      val ip = input.readLine()
      println(ip)
    }
  }
  var message = ""
  while (message != "!quit") {
    val message = readLine()
    output.println(message)
  }
  s.close()
}
