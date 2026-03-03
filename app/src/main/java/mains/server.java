package mains;

import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Optional;

import Communication.RequestInputStream;
import backend.AuthService;
import backend.Messagehandler;
import models.*;

public class server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private Messagehandler msgHandler;
  
  public server(ServerSocket ss) throws IOException {
    serverSocket = ss;
    msgHandler = Messagehandler.getInstance();
    newListener();
  }

  public void run() {
    try {
      SSLSocket socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      numConnectedClients++;
      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      System.out.println(subject);
      System.out.println(subject);
      System.out.println(subject);
      System.out.println(numConnectedClients + " concurrent connection(s)\n");

      ObjectOutputStream out = null;
      RequestInputStream in = null;
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new RequestInputStream(socket.getInputStream());
      
      Request clientMsg = null;

      
      Optional<User> opUser = AuthService.CNtoUser(subject.substring(3));
      if (opUser.isPresent()) {
        User user = opUser.get();
        while ((clientMsg = in.readObject()) != null) {
          Response response = msgHandler.handle(clientMsg, user);
          out.writeObject(response);
          out.flush();
          System.out.println("done\n");
        }
      } else {
          Response response = new Response("User not found :(");
          out.writeObject(response);
          out.flush();
      }

      in.close();
      out.close();
      socket.close();
      numConnectedClients--;
      System.out.println("client disconnected");
      System.out.println(numConnectedClients + " concurrent connection(s)\n");
    } catch (IOException e) {
      System.out.println("Client died: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }
  
  private void newListener() { (new Thread(this)).start(); } // calls run()

  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port, 0, InetAddress.getByName(null));
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("PKCS12");
        KeyStore ts = KeyStore.getInstance("PKCS12");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        ks.load(new FileInputStream("stores/serverkeystore"), password);  
        // truststore password (storepass)
        ts.load(new FileInputStream("stores/servertruststore"), password); 
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }
}
