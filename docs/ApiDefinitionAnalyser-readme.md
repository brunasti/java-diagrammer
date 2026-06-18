# ApiDefinitionAnalyser

Scan a tree of Java source files and extract every Spring REST API endpoint defined
with `@RestController`, `@RequestMapping`, and the HTTP-method-specific mapping
annotations (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`,
`@PatchMapping`).

By Paolo Brunasti

---

## Overview

The tool reads `.java` source files as plain text, so it does **not** require the
project to be compiled first.  For each file it:

1. Detects whether the class is annotated with `@RestController`.
2. Reads the class-level `@RequestMapping` value as the **base path**.
3. Scans every method for a mapping annotation and combines the base path with the
   method-level path to produce the full URL.
4. Collects all results and writes two sections to the output:
   - a list of every `@RestController` class with its base path;
   - a sorted table of every API endpoint (HTTP method + full URL + class::method).

---


## Running

### Via the shell script (recommended)

```bash
./run-api-analyser.sh [--build] <source-path> [-r] [-o <file>] [-c <file>] [-d [level]]
```

| Argument | Description |
|---|---|
| `<source-path>` | Root directory of the Java source files to analyse (**required**) |
| `-r`, `--recursive` | Recursively scan all subdirectories under `<source-path>` |
| `-o <file>` | Write output to a file instead of stdout |
| `-c <file>` | JSON configuration file |
| `-d [level]` | Enable debug output; optional numeric level (e.g. `-d 2`) |
| `-h` / `-?` | Print the built-in help / quick-reference |
| `--build` | Run `mvn compile` before executing (useful after code changes) |
| `--help` | Print the script-level help message |

#### Examples

Scan a single package directory (non-recursive):
```bash
./run-api-analyser.sh /path/to/project/src/main/java/com/example/api
```

Scan the entire source tree and save the report:
```bash
./run-api-analyser.sh /path/to/project/src/main/java -r -o api-report.txt
```

Compile first, then scan with debug level 2:
```bash
./run-api-analyser.sh --build /path/to/project/src/main/java -r -d 2 -o api-report.txt
```

Use a configuration file to exclude certain packages:
```bash
./run-api-analyser.sh /path/to/project/src/main/java -r -c config.json -o api-report.txt
```

---

### Via Java directly

```bash
java -cp target/classes:\
$HOME/.m2/repository/com/thoughtworks/qdox/qdox/2.0.3/qdox-2.0.3.jar:\
$HOME/.m2/repository/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar:\
$HOME/.m2/repository/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar:\
$HOME/.m2/repository/org/apache/logging/log4j/log4j-api/2.25.4/log4j-api-2.25.4.jar:\
$HOME/.m2/repository/org/apache/logging/log4j/log4j-core/2.25.4/log4j-core-2.25.4.jar:\
/path/to/java-utils/target/it.brunasti.java.utils.Utils-1.0.1.jar \
    it.brunasti.java.diagrammer.spring.api.ApiDefinitionAnalyserMain \
    /path/to/project/src/main/java -r -o api-report.txt
```

---

## Output format

The report has two sections.

### Section 1 – RestController Classes

Lists every class annotated with `@RestController`, sorted by package and class name.

```
--- RestController Classes ---

BASE PATH                                 CLASS
--------------------------------------------------------------------------------
(no base mapping)                         com.example.HealthController
/api/users                                com.example.UserController
```

`(no base mapping)` means the class has no class-level `@RequestMapping`.

### Section 2 – API Endpoints

Lists every HTTP endpoint, sorted by URL then HTTP method.  The full URL is the
concatenation of the class-level base path and the method-level path.

```
--- API Endpoints ---

METHOD      URL                                                           CLASS :: METHOD
------------------------------------------------------------------------------------------------------------------------
POST        /api/users                                                    com.example.UserController::createUser
GET         /api/users/search                                             com.example.UserController::search
DELETE      /api/users/{id}                                               com.example.UserController::deleteUser
GET         /api/users/{id}                                               com.example.UserController::getUser
GET         /health                                                       com.example.HealthController::health
GET         /ready                                                        com.example.HealthController::ready
```

---

## Package structure

```
it.brunasti.java.diagrammer.spring.api
├── ApiDefinitionAnalyserMain   CLI entry point (argument parsing, I/O wiring)
├── ApiDefinitionAnalyser       Core analysis engine (text scanning)
├── ApiEndpoint                 Data class – one discovered HTTP endpoint
└── RestControllerInfo          Data class – one discovered @RestController class
```

### `ApiDefinitionAnalyserMain`

Apache Commons CLI entry point.  Parses the command-line arguments and wires the
input path, output stream, configuration file, recursive flag, and debug level into
`ApiDefinitionAnalyser`.

### `ApiDefinitionAnalyser`

The analysis engine.  Key responsibilities:

| Method | Description |
|---|---|
| `setRecursive(boolean)` | Toggle between single-directory and full tree scan |
| `analyseFile(String)` | Read one `.java` file and extract its endpoints |
| `loadStructure(...)` | Walk the source tree and call `analyseFile` on every `.java` file found |
| `generateOutput(...)` | Write the two-section report to the configured `PrintStream` |
| `process(String)` | Convenience entry point – calls `loadStructure` then `generateOutput` |

Text-parsing helpers used inside `analyseFile`:

| Method | Purpose |
|---|---|
| `isRestController(content)` | Checks whether the file contains `@RestController` as an actual annotation (not inside a string literal or comment) |
| `extractPackageName(content)` | Reads the `package ...;` declaration |
| `extractClassName(content)` | Reads the `class ClassName` declaration |
| `findClassBodyStart(content, className)` | Locates the opening `{` of the class body, used to distinguish class-level from method-level annotations |
| `extractUrlFromAnnotationText(text)` | Extracts the first quoted URL string from inside an annotation's parentheses |
| `findAnnotationCloseParen(text)` | Finds the annotation's closing `)` while correctly skipping `)` characters that appear inside quoted strings (e.g. regex path variables like `/{id:[0-9]+}`) |
| `extractFunctionName(block)` | Extracts the method name from the signature that follows the annotation |
| `resolveHttpMethod(annotationType, block)` | Maps the annotation prefix (`Get`, `Post`, …) to an HTTP verb; for `@RequestMapping` reads the `method = RequestMethod.XXX` attribute |

### `ApiEndpoint`

Holds the data for a single discovered endpoint:

| Field | Content |
|---|---|
| `packageName` | Java package of the controller class |
| `className` | Simple name of the controller class |
| `functionName` | Method name |
| `httpMethod` | `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, or `REQUEST` |
| `url` | Full URL = class base path + method path |

### `RestControllerInfo`

Holds the data for a discovered controller:

| Field | Content |
|---|---|
| `packageName` | Java package of the controller class |
| `className` | Simple name of the controller class |
| `baseUrl` | Value of the class-level `@RequestMapping`, or `""` if absent |

---

## Building

```bash
mvn compile
```

The tool is part of the main `java-diagrammer` Maven project.  No separate build step
is required.

---

## Configuration file

An optional JSON file (passed via `-c`) can be used to steer the analysis.
The structure follows the same convention as the rest of the `java-diagrammer` project:

```json
{
  "exclude": {
    "packages": [
      { "package": "com.example.internal." }
    ],
    "classes": [
      { "class": "com.example.LegacyController" }
    ]
  }
}
```

> **Note:** configuration-driven exclusions are reserved for future implementation.
> The `-c` option is accepted and parsed but the exclusion logic is not yet active.

---

## Supported Spring annotations

| Annotation | Handled as |
|---|---|
| `@RestController` | Marks the class as a REST controller |
| `@RequestMapping` on the class | Sets the base URL path for all methods |
| `@GetMapping` | `GET` endpoint |
| `@PostMapping` | `POST` endpoint |
| `@PutMapping` | `PUT` endpoint |
| `@DeleteMapping` | `DELETE` endpoint |
| `@PatchMapping` | `PATCH` endpoint |
| `@RequestMapping` on a method | Endpoint with HTTP method read from `method = RequestMethod.XXX`; shown as `REQUEST` if no method attribute is present |

---

## Known limitations

- **Text-based parsing** — the tool reads `.java` files as plain text and does not
  build a full AST.  Unusual formatting (e.g. annotation attributes split across many
  lines, or URLs built from constants) may not be parsed correctly.
- **First quoted string wins** — when an annotation has multiple string attributes
  (e.g. `produces = "application/json"` before `value = "/path"`), the tool picks the
  first quoted string.  Place the URL value first, or use the `value = "..."` named
  form.
- **Configuration exclusions** — the `-c` option is wired but exclusion logic is not
  yet implemented.
- **Compiled-only projects** — because the tool reads `.java` source files, it cannot
  analyse projects for which source code is not available.
