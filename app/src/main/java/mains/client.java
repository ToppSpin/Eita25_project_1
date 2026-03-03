package mains;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.*;

import javax.net.ssl.*;

import Communication.ResponseInputStream;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class client {

  private static BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
  public static void main(String[] args) throws Exception {
    String host = null;
    int port = -1;
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }
    if (args.length < 1) {
      System.out.println("USAGE: java client [host] port");
      System.exit(-1);
    }
    try { /* get input parameters */
      if (args.length == 1) port = Integer.parseInt(args[0]);
      else {
        host = args[0];
        port = Integer.parseInt(args[1]);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("USAGE: java client [host] port");
      System.exit(-1);
    }

    try {
      SSLSocketFactory factory = null;
      try {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        KeyStore ts = KeyStore.getInstance("PKCS12");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        System.out.println("user.dir = " + System.getProperty("user.dir"));
        while (true) {
          try {
            String filePath = FilePicker.pickFile();
            if (filePath == null) {
              System.err.println("User cancelled");
              continue;
            }
            System.out.println("Enter password: \n:");
            char[] password = read.readLine().toCharArray();
            FileInputStream fis = new FileInputStream(filePath);
            // keystore password (storepass)
            ks.load(fis, "password".toCharArray()); 
            kmf.init(ks, "password".toCharArray());  // user password (keypass)
            break;
          } catch (FileNotFoundException e) {
            System.out.println("File not found");
          } catch (IOException e) {
            System.out.println("File not found");
          }
        }
        while (true) {
          try {
            String filePath = FilePicker.pickFile();
            if (filePath == null) {
              System.err.println("User cancelled");
              continue;
            }
            System.out.println("Enter password: \n:");
            char[] password = read.readLine().toCharArray();
            FileInputStream fis = new FileInputStream(filePath);
            // truststore password (storepass)
            ts.load(fis, "password".toCharArray());  
            break;
          } catch (FileNotFoundException e) {
            
          } catch (IOException e) {

          }
        }
        tmf.init(ts); // keystore can be used as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        factory = ctx.getSocketFactory();
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
      System.out.println("\nsocket before handshake:\n" + socket + "\n");

      /*
       * send http request
       *
       * See SSLSocketClient.java for more information about why
       * there is a forced handshake here when using PrintWriters.
       */

      socket.startHandshake();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      BigInteger serial = ((X509Certificate) cert[0]).getSerialNumber();
      System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
      System.out.println("certificate name (Issuer DN field) on certificate received from server:\n" + issuer + "\n");
      System.out.println("serial = \n" + serial + "\n");
      System.out.println("socket after handshake:\n" + socket + "\n");
      System.out.println("secure connection established\n\n");

      BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      ResponseInputStream in = new ResponseInputStream(socket.getInputStream());
      clientHandler handler = new clientHandler(read, out, in);
      handler.run();
      in.close();
      out.close();
      read.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
