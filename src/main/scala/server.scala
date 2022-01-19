import java.net.{ServerSocket, Socket}
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

object server extends App {
  case class ChatClient (name: String, s: Socket, input: BufferedReader, output: PrintStream)
  val clients = new ConcurrentHashMap[String, ChatClient]().asScala

  Future { checkConnections() }
  while(true) {
    for ((name, client) <- clients) {
      establishChat(client)
    }
  }

  def checkConnections(): Unit = {
    val serverSock = new ServerSocket(3536)
    while(true) {
      val s = serverSock.accept()
      val input = new BufferedReader(new InputStreamReader(s.getInputStream))
      val output = new PrintStream(s.getOutputStream)
      Future {
        output.println("Enter your name : ")
        val name = input.readLine()
        val client = ChatClient(name, s, input, output)
        clients += name -> client
        println("Client " + name + " Connected")
        output.println("Hi! " + name)
      }
    }
  }

  def safeRead(input: BufferedReader): Option[String] = {
    if(input.ready()) Some(input.readLine()) else None
  }

  def establishChat(client: ChatClient) : Unit = {
    safeRead(client.input).foreach { ip =>
      if (ip == "!quit") {
        client.s.close()
        println("Client " + client.name + " Disconnected")
        clients -= client.name
      } else {
        for ((n, cli) <- clients) {
          cli.output.println(client.name + " : " + ip)
        }
      }
    }
  }

}
