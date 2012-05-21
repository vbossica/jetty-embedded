package org.vbossica.jetty;

import java.security.ProtectionDomain;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Credit goes to Jochen Wierum for his original work.
 */
public class EmbeddedServer {

  @Parameter( names = "-port", description = "HTTP port of the server" )
  private Integer port = 8080;
  @Parameter( names = "-contextPath", description = "context path of the embedded web application" )
  private String contextPath = "/";
  @Parameter( names = "-maxIdleTime", description = "maxIdleTime (see documentation)" )
  private Integer maxIdleTime = 0;
  @Parameter( names = "-soLingerTime", description = "soLingerTime (see documentation)" )
  private Integer soLingerTime = -1;
  @Parameter( names = { "-help", "-?" }, description = "prints this message", hidden = true )
  private Boolean help = false;

  private void run() {
    Server server = new Server();
    SocketConnector connector = new SocketConnector();

    connector.setMaxIdleTime( maxIdleTime );
    connector.setSoLingerTime( soLingerTime );
    connector.setPort( port );
    server.setConnectors( new Connector[] { connector } );

    WebAppContext context = new WebAppContext();
    context.setServer( server );
    if ( !contextPath.startsWith( "/" ) ) {
      contextPath = "/" + contextPath;
    }
    context.setContextPath( contextPath );

    ProtectionDomain protectionDomain = EmbeddedServer.class.getProtectionDomain();
    context.setWar( protectionDomain.getCodeSource().getLocation().toExternalForm() );

    server.setHandler( context );
    try {
      server.start();
      System.in.read();
      server.stop();
      server.join();
    } catch ( Exception ex ) {
      ex.printStackTrace();
      System.exit( 100 );
    }
  }

  public static void main( String[] args ) throws Exception {
    EmbeddedServer server = new EmbeddedServer();
    JCommander commander = new JCommander( server, args );
    if ( server.help ) {
      commander.usage();
    } else {
      server.run();
    }
  }

}