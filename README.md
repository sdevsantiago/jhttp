# JHTTP

![Java](https://img.shields.io/badge/Java-25-orange)
![Maven](https://img.shields.io/badge/build-Maven-blue)
![HTTP](https://img.shields.io/badge/protocol-HTTP%2FHTTPS-2ea44f)
![License](https://img.shields.io/badge/license-GPL--3.0-blue)

JHTTP is a lightweight HTTP/HTTPS server written in Java. It is built as an
educational, extensible server implementation that exposes the core mechanics
behind a web server: sockets, request decoding, response encoding, static file
serving, TLS setup, concurrency, and request processing pipelines.

The project does not aim to replace production-grade servers such as Apache,
NGINX, Tomcat, or Jetty. Its goal is to provide a compact and readable Java
implementation of the essential pieces that make an HTTP server work.

## Features

- HTTP/1.1 request parsing.
- Optional HTTPS support using a PKCS12 certificate.
- Automatic HTTP to HTTPS redirection when HTTPS is enabled.
- Static file serving from a configurable root directory.
- `index.html` resolution for directory-style requests.
- MIME type detection for common static assets.
- Custom HTML error pages.
- Modular request pipeline based on handlers.
- Runtime module discovery through annotations.
- Virtual-thread based connection handling.
- External configuration through `.properties` files.
- Maven build with development and production profiles.
- Console logging through Log4j2.

## Requirements

- Java 25 or newer.
- Maven 3.9+.

Check your local versions:

```bash
java --version
mvn --version
```

## Quick Start

Clone the repository:

```bash
git clone https://github.com/sdevsantiago/jhttp.git
cd jhttp
```

Create a web root directory and add an `index.html` file:

```bash
mkdir www
echo "<h1>Hello from JHTTP</h1>" > www/index.html
```

Update `src/main/resources/server.properties` so `root.directory` points to
that directory:

```properties
root.directory = ./www
http.port = 8080
https.disabled = true
https.port = 8443
https.certificate.path = jhttp.p12
https.certificate.password = 123456
modules =
```

Build the project:

```bash
mvn clean package
```

Run the server:

```bash
java -jar target/jhttp-1.0.jar
```

Open:

```text
http://localhost:8080/
```

## Configuration

JHTTP reads its runtime configuration from `server.properties` by default. A
custom configuration file can also be provided as the first command-line
argument:

```bash
java -jar target/jhttp-1.0.jar path/to/server.properties
```

Available properties:

| Property | Default | Description |
| --- | --- | --- |
| `root.directory` | `/www` | Directory used as the static file root. It must exist, be readable, and be a directory. |
| `http.port` | `8080` | Port used by the HTTP socket. |
| `https.disabled` | `true` | Disables HTTPS when set to `true`; enables HTTPS when set to `false`. |
| `https.port` | `8443` | Port used by the HTTPS socket when HTTPS is enabled. |
| `https.certificate.path` | `jhttp.p12` | Path to the PKCS12 certificate used for TLS. |
| `https.certificate.password` | `123456` | Password used to load the PKCS12 certificate. |
| `modules` | empty | Comma-separated list of pipeline modules to enable. Keep this property present even when no modules are enabled. |

## HTTPS

To enable HTTPS, provide a valid PKCS12 certificate and set
`https.disabled = false`:

```properties
https.disabled = false
https.port = 8443
https.certificate.path = jhttp.p12
https.certificate.password = changeit
```

When HTTPS is enabled:

- JHTTP opens an HTTPS socket on `https.port`.
- The normal request pipeline runs over HTTPS.
- The HTTP port remains open only to redirect requests to HTTPS.
- Redirect responses use `308 Permanent Redirect`.

For local development, a self-signed certificate can be generated with
`keytool`:

```bash
keytool -genkeypair \
  -alias jhttp \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore jhttp.p12 \
  -validity 365 \
  -storepass changeit
```

Do not commit real production certificates or passwords to the repository.

## Request Pipeline

JHTTP processes requests through an ordered handler pipeline.

Default pipeline:

```text
StaticContentHandler -> configured modules -> FallbackHandler
```

When HTTPS is enabled, the HTTP socket uses a redirect-only pipeline:

```text
HttpsRedirectHandler
```

Each handler receives a shared `Context` containing:

- the client socket,
- the decoded `HttpRequest`,
- the current `HttpResponse`, if one has already been generated.

This design keeps the server core small while allowing new behavior to be added
without changing the bootstrap or socket code.

## Modules

Modules are custom pipeline handlers discovered at startup. A module must:

1. Implement `sdevsantiago.jhttp.pipeline.Handler`.
2. Be annotated with `@Module`.
3. Provide a public no-argument constructor.
4. Be available under the scanned package:
   `sdevsantiago.jhttp.module.added`.

Example:

```java
package sdevsantiago.jhttp.module.added;

import sdevsantiago.jhttp.module.annotations.Module;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Handler;

@Module(name = "customHeaders")
public class CustomHeadersModule implements Handler {

    @Override
    public void handle(Context context) {
        if (context.hasResponse()) {
            context.getResponse()
                .headers()
                .put("X-Powered-By", "JHTTP");
        }
    }
}
```

Enable it in `server.properties`:

```properties
modules = customHeaders
```

Multiple modules can be enabled with a comma-separated list:

```properties
modules = auth,customHeaders,metrics
```

## Project Structure

```text
src/main/java/sdevsantiago/jhttp
├── JHTTP.java                  # Application entry point
├── config                      # Configuration loading and server bootstrap
├── core                        # Acceptor, executor, HTTP decoder and encoder
├── http                        # HTTP domain model: requests, responses, headers
├── module                      # Module annotation, registry and loader
├── pipeline                    # Handler pipeline and context
└── util                        # Utility classes

src/main/resources
├── application.properties      # Application defaults
├── server.properties           # Runtime server configuration
├── log4j2.xml                  # Logging configuration
└── templates/error.html        # Error page template
```

## Build Profiles

The project defines two Maven profiles:

| Profile | Log level | Usage |
| --- | --- | --- |
| `dev` | `debug` | Default profile for development. |
| `prod` | `info` | Production-oriented build profile. |

Build with the production profile:

```bash
mvn clean package -P prod
```

## Supported HTTP Elements

Supported HTTP version:

- `HTTP/1.1`

Recognized HTTP methods:

- `GET`
- `POST`
- `PUT`
- `DELETE`
- `HEAD`
- `OPTIONS`
- `PATCH`

Built-in status responses include:

- `200 OK`
- `308 Permanent Redirect`
- `404 Not Found`
- `500 Internal Server Error`

## Known Limitations

JHTTP is intentionally small and educational. Current limitations include:

- HTTP/1.1 only.
- One request per connection; sockets are closed after each response.
- Static files are read fully into memory before being sent.
- No HTTP/2, HTTP/3, WebSocket, CGI, Servlet API, compression, or caching layer.
- Module discovery is classpath-based and limited to the configured package.
- Malformed requests currently fall back to generic error handling.

## Roadmap Ideas

- Add `400 Bad Request` handling for malformed requests.
- Add stronger static path traversal protection.
- Stream large files in chunks instead of loading them entirely in memory.
- Support persistent HTTP/1.1 connections.
- Add automated unit and integration tests.
- Add module-specific configuration.
- Add runtime metrics.
- Expand MIME type support.

## License

This project is licensed under the GNU General Public License v3.0. See
[LICENSE](LICENSE) for details.
